
```scala
package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model._

import scala.collection.JavaConversions._
import ohnosequences.awstools.ec2.{Utils}
import ohnosequences.awstools.regions.Region._

import ohnosequences.awstools.{ec2 => awstools}

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.autoscaling.model.Tag
import java.util.Date
import com.amazonaws.internal.StaticCredentialsProvider
import scala.Some


class AutoScaling(val as: AmazonAutoScaling, ec2: ohnosequences.awstools.ec2.EC2) { autoscaling =>

  def shutdown() {
    as.shutdown()
  }

  def fixAutoScalingGroupUserData(group: AutoScalingGroup, fixedUserData: String): AutoScalingGroup = {
    val specs = group.launchingConfiguration.instanceSpecs.copy(userData = fixedUserData)
    val lc = group.launchingConfiguration.copy(instanceSpecs = specs)
    val fixedGroup = group.copy(launchingConfiguration = lc)
    fixedGroup
  }

  def createLaunchingConfiguration(launchConfiguration: ohnosequences.awstools.autoscaling.LaunchConfiguration) {
    try {

      var lcr = new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(launchConfiguration.name)
        .withImageId(launchConfiguration.instanceSpecs.amiId)
        .withInstanceType(launchConfiguration.instanceSpecs.instanceType.toString)
        .withUserData(Utils.base64encode(launchConfiguration.instanceSpecs.userData))
        .withKeyName(launchConfiguration.instanceSpecs.keyName)
        .withSecurityGroups(launchConfiguration.instanceSpecs.securityGroups)
        .withInstanceMonitoring(new InstanceMonitoring().withEnabled(launchConfiguration.instanceSpecs.instanceMonitoring))
        .withBlockDeviceMappings(
        launchConfiguration.instanceSpecs.deviceMapping.map{ case (key, value) =>
          new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
        }.toList)


      lcr = launchConfiguration.purchaseModel match {
        case Spot(price) => lcr.withSpotPrice(price.toString)
        case SpotAuto => {
          val price = SpotAuto.getCurrentPrice(ec2, launchConfiguration.instanceSpecs.instanceType)
          lcr.withSpotPrice(price.toString)
        }
        case OnDemand => lcr
      }

      lcr = launchConfiguration.instanceSpecs.instanceProfile match {
        case Some(name) => lcr.withIamInstanceProfile(name)
        case None => lcr
      }

      as.createLaunchConfiguration(lcr)

    } catch {
      case e: AlreadyExistsException => ;
    }
  }

  def createAutoScalingGroup(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup) = {
    getAutoScalingGroupByName(autoScalingGroup.name) match {
      case Some(group) => {
        println("aws-scala-tools WARNING: group with name " + autoScalingGroup.name + " is already exists")
        group
      }
      case None => {
        createLaunchingConfiguration(autoScalingGroup.launchingConfiguration)
        as.createAutoScalingGroup(new CreateAutoScalingGroupRequest()
          .withAutoScalingGroupName(autoScalingGroup.name)
          .withLaunchConfigurationName(autoScalingGroup.launchingConfiguration.name)
          .withAvailabilityZones(autoScalingGroup.availabilityZones)
          .withMaxSize(autoScalingGroup.maxSize)
          .withMinSize(autoScalingGroup.minSize)
          .withDesiredCapacity(autoScalingGroup.desiredCapacity)
        )
      }
    }

  }

  def describeTags(name: String): List[awstools.Tag] = {
    as.describeTags(new DescribeTagsRequest()
      .withFilters(
        new Filter()
          .withName("auto-scaling-group")
          .withValues(name)
      )
    ).getTags.toList.map { tagDescription =>
      awstools.Tag(tagDescription.getKey, tagDescription.getValue)
    }
  }

  def getTagValue(groupName: String, tagName: String): Option[String] = {
    describeTags(groupName).find(_.name.equals(tagName)).map(_.value)
  }




  def createTags(name: String, tags: ohnosequences.awstools.ec2.Tag*) {
    val asTags = tags.map { tag =>
      new Tag().withKey(tag.name).withValue(tag.value).withResourceId(name).withPropagateAtLaunch(true).withResourceType("auto-scaling-group")
    }
    as.createOrUpdateTags(new CreateOrUpdateTagsRequest()
      .withTags(asTags)
    )
  }

  def getLaunchConfigurationByName(name: String): Option[ohnosequences.awstools.autoscaling.LaunchConfiguration] = {
    as.describeLaunchConfigurations(new DescribeLaunchConfigurationsRequest()
      .withLaunchConfigurationNames(name)
    ).getLaunchConfigurations.map {
      lc => ohnosequences.awstools.autoscaling.LaunchConfiguration.fromAWS(lc)
    }.headOption
  }

  def getAutoScalingGroupByName(name: String): Option[ohnosequences.awstools.autoscaling.AutoScalingGroup] = {
    as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
      .withAutoScalingGroupNames(name)
    ).getAutoScalingGroups.flatMap {asg =>
      ohnosequences.awstools.autoscaling.AutoScalingGroup.fromAWS(asg, autoscaling)
    }.headOption
  }

  def describeLaunchConfigurations(): List[ohnosequences.awstools.autoscaling.LaunchConfiguration] = {
    as.describeLaunchConfigurations().getLaunchConfigurations.map {lc => ohnosequences.awstools.autoscaling.LaunchConfiguration.fromAWS(lc)}.toList
  }

  def describeAutoScalingGroups(): List[ohnosequences.awstools.autoscaling.AutoScalingGroup] = {
    as.describeAutoScalingGroups().getAutoScalingGroups.map {asg =>
      ohnosequences.awstools.autoscaling.AutoScalingGroup.fromAWS(asg, autoscaling)
    }.flatten.toList
  }

  def deleteLaunchConfiguration(name: String) {
    try {
      as.deleteLaunchConfiguration(
        new DeleteLaunchConfigurationRequest()
          .withLaunchConfigurationName(name)
      )
    } catch {
      case e: AmazonServiceException => ()
    }
  }

  def deleteAutoScalingGroup(name: String) {
    getAutoScalingGroupByName(name).map(deleteAutoScalingGroup)
  }

  def setDesiredCapacity(group: ohnosequences.awstools.autoscaling.AutoScalingGroup, capacity: Int) {
    as.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(group.name)
      .withDesiredCapacity(capacity)
    )
  }

  def getCreatedTime(name: String): Option[Date] = {
    as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
      .withAutoScalingGroupNames(name)
    ).getAutoScalingGroups.headOption.map(_.getCreatedTime)
  }



//  * <b>NOTE:</b> To remove all instances before calling
//    * DeleteAutoScalingGroup, you can call UpdateAutoScalingGroup to set the
//  * minimum and maximum size of the AutoScalingGroup to zero.
//  * </p>
  def deleteAutoScalingGroup(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup) {
    try {
      as.deleteAutoScalingGroup(
        new DeleteAutoScalingGroupRequest()
          .withAutoScalingGroupName(autoScalingGroup.name)
          .withForceDelete(true)
      )
    } catch {
      case e: AmazonServiceException   => ;
    }
    finally {
      deleteLaunchConfiguration(autoScalingGroup.launchingConfiguration.name)
    }

  }

}

object AutoScaling {

  def create(ec2: ohnosequences.awstools.ec2.EC2): AutoScaling = {
    create(new InstanceProfileCredentialsProvider(), ec2)
  }

  def create(credentialsFile: File, ec2: ohnosequences.awstools.ec2.EC2): AutoScaling = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)), ec2)
  }

  def create(accessKey: String, secretKey: String, ec2: ohnosequences.awstools.ec2.EC2): AutoScaling = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)), ec2)
  }

  def create(credentials: AWSCredentialsProvider, ec2: ohnosequences.awstools.ec2.EC2, region: ohnosequences.awstools.regions.Region = Ireland): AutoScaling = {
    val asClient = new AmazonAutoScalingClient(credentials)
    asClient.setRegion(region)
    new AutoScaling(asClient, ec2)
  }
}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: ../dynamodb/DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: ../dynamodb/DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: ../dynamodb/Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: ../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: ../../../../../test/scala/ohnosequences/awstools/InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md