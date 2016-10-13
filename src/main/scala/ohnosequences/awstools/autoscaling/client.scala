package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model._
import com.amazonaws.AmazonServiceException
import com.amazonaws.services.autoscaling.model.Tag
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.services.ec2.model.{ DescribeAvailabilityZonesRequest, Filter => ec2Filter }

import ohnosequences.awstools.regions._
import ohnosequences.awstools.ec2._

import java.util.Date
import scala.util.Try
import scala.collection.JavaConversions._


case class ScalaAutoScalingClient(val as: AmazonAutoScaling) { autoscaling =>

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

  def createLaunchingConfiguration(launchConfiguration: ohnosequences.awstools.autoscaling.LaunchConfiguration, ec2: ScalaEC2Client) {
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
        case OnDemand => lcr
        case pm @ Spot(_, _) => {
          val price = pm.price(ec2, launchConfiguration.launchSpecs.instanceSpecs.instanceType)
          lcr.withSpotPrice(price.toString)
        }
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

  def getAllAvailableZones(ec2: ScalaEC2Client): List[String] = {
    ec2.asJava.describeAvailabilityZones(
      new DescribeAvailabilityZonesRequest()
        .withFilters(new ec2Filter("state", List("available")))
    )
    .getAvailabilityZones
    .toList.map{ _.getZoneName }
  }

  def createAutoScalingGroup(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup, ec2: ScalaEC2Client) = {
    getAutoScalingGroupByName(autoScalingGroup.name) match {
      case Some(group) => {
        println("aws-scala-tools WARNING: group with name " + autoScalingGroup.name + " is already exists")
        group
      }
      case None => {
        val configZones = autoScalingGroup.availabilityZones

        createLaunchingConfiguration(autoScalingGroup.launchConfiguration, ec2)
        as.createAutoScalingGroup(new CreateAutoScalingGroupRequest()
          .withAutoScalingGroupName(autoScalingGroup.name)
          .withLaunchConfigurationName(autoScalingGroup.launchConfiguration.name)
          .withAvailabilityZones(
            if (configZones.nonEmpty) configZones
            else getAllAvailableZones(ec2)
          )
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
