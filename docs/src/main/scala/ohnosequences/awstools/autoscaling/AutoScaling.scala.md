
```scala
package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model._
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.autoscaling.model.Tag
import com.amazonaws.internal.StaticCredentialsProvider

import ohnosequences.awstools.regions._
import ohnosequences.awstools.ec2._

import java.util.Date
import scala.util.Try
import scala.collection.JavaConversions._


class AutoScaling(val as: AmazonAutoScaling, val ec2: EC2) { autoscaling =>

  def shutdown(): Unit = { as.shutdown() }

  def fixAutoScalingGroupUserData(group: AutoScalingGroup, fixedUserData: String): AutoScalingGroup = {
    val lc = group.launchConfiguration
    val ls = lc.launchSpecs

    group.copy( launchConfiguration =
      lc.copy( launchSpecs =
        LaunchSpecs(ls.instanceSpecs)(
          ls.keyName,
          userData = fixedUserData,
          ls.instanceProfile,
          ls.securityGroups,
          ls.instanceMonitoring,
          ls.deviceMapping
        )
      )
    )
  }

  def createLaunchingConfiguration(launchConfiguration: ohnosequences.awstools.autoscaling.LaunchConfiguration) {
    try {

      var lcr = new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(launchConfiguration.name)
        .withImageId(launchConfiguration.launchSpecs.instanceSpecs.ami.id)
        .withInstanceType(launchConfiguration.launchSpecs.instanceSpecs.instanceType.toString)
        .withUserData(base64encode(launchConfiguration.launchSpecs.userData))
        .withKeyName(launchConfiguration.launchSpecs.keyName)
        .withSecurityGroups(launchConfiguration.launchSpecs.securityGroups)
        .withInstanceMonitoring(new InstanceMonitoring().withEnabled(launchConfiguration.launchSpecs.instanceMonitoring))
        .withBlockDeviceMappings(
        launchConfiguration.launchSpecs.deviceMapping.map{ case (key, value) =>
          new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
        }.toList)


      lcr = launchConfiguration.purchaseModel match {
        case Spot(price) => lcr.withSpotPrice(price.toString)
        case SpotAuto => {
          val price = SpotAuto.getCurrentPrice(ec2, launchConfiguration.launchSpecs.instanceSpecs.instanceType)
          lcr.withSpotPrice(price.toString)
        }
        case OnDemand => lcr
      }

      lcr = launchConfiguration.launchSpecs.instanceProfile match {
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
        createLaunchingConfiguration(autoScalingGroup.launchConfiguration)
        as.createAutoScalingGroup(new CreateAutoScalingGroupRequest()
          .withAutoScalingGroupName(autoScalingGroup.name)
          .withLaunchConfigurationName(autoScalingGroup.launchConfiguration.name)
          .withAvailabilityZones(autoScalingGroup.availabilityZones)
          .withMaxSize(autoScalingGroup.size.max)
          .withMinSize(autoScalingGroup.size.min)
          .withDesiredCapacity(autoScalingGroup.size.desired)
        )
      }
    }

  }

  def describeTags(name: String): List[InstanceTag] = {
    as.describeTags(new DescribeTagsRequest()
      .withFilters(
        new Filter()
          .withName("auto-scaling-group")
          .withValues(name)
      )
    ).getTags.toList.map { tagDescription =>
      InstanceTag(tagDescription.getKey, tagDescription.getValue)
    }
  }

  def getTagValue(groupName: String, tagName: String): Option[String] = {
    describeTags(groupName).find(_.name.equals(tagName)).map(_.value)
  }




  def createTags(name: String, tags: InstanceTag*) {
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

  def getCreatedTimeTry(name: String): Try[Date] = {
    Try {
      as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(name)
      ).getAutoScalingGroups.head.getCreatedTime
    }
  }


  //  NOTE: To remove all instances before calling
  //  DeleteAutoScalingGroup, you can call UpdateAutoScalingGroup to set the
  //  minimum and maximum size of the AutoScalingGroup to zero.
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
      deleteLaunchConfiguration(autoScalingGroup.launchConfiguration.name)
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

  def create(credentials: AWSCredentialsProvider, ec2: ohnosequences.awstools.ec2.EC2, region: Region = Region.Ireland): AutoScaling = {
    val asClient = new AmazonAutoScalingClient(credentials)
    asClient.setRegion(region.toAWSRegion)
    new AutoScaling(asClient, ec2)
  }
}

```




[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md