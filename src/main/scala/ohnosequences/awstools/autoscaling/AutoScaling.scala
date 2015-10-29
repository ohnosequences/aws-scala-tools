package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model._


import scala.collection.JavaConversions._
// import ohnosequences.awstools.ec2.{Utils}
import ohnosequences.awstools.regions.Region._

import ohnosequences.awstools.{ec2 => awstools}

import com.amazonaws.AmazonServiceException
import com.amazonaws.services.autoscaling.model.Tag
import java.util.Date
import com.amazonaws.internal.StaticCredentialsProvider
import scala.Some
import scala.util.Try


class AutoScaling(val as: AmazonAutoScaling, ec2: ohnosequences.awstools.ec2.EC2) { autoscaling =>

  def shutdown() {
    as.shutdown()
  }

  def fixAutoScalingGroupUserData(group: AutoScalingGroup, fixedUserData: String): AutoScalingGroup = {
    val specs = group.launchingConfiguration.launchSpecs.copy(userData = fixedUserData)
    val lc = group.launchingConfiguration.copy(launchSpecs = specs)
    val fixedGroup = group.copy(launchingConfiguration = lc)
    fixedGroup
  }

  def createLaunchingConfiguration(launchConfiguration: ohnosequences.awstools.autoscaling.LaunchConfiguration) {
    try {

      var lcr = new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(launchConfiguration.name)
        .withImageId(launchConfiguration.launchSpecs.amiId)
        .withInstanceType(launchConfiguration.launchSpecs.instanceType.toString)
        .withUserData(Utils.base64encode(launchConfiguration.launchSpecs.userData))
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
          val price = SpotAuto.getCurrentPrice(ec2, launchConfiguration.launchSpecs.instanceType)
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

  def getCreatedTimeTry(name: String): Try[Date] = {
    Try {
      as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(name)
      ).getAutoScalingGroups.head.getCreatedTime
    }
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
