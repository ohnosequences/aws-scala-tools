package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model
import com.amazonaws.services.autoscaling.model._
// import com.amazonaws.services.autoscaling.model.Tag
// import com.amazonaws.services.ec2.model.{ DescribeAvailabilityZonesRequest, Filter => ec2Filter }

import ohnosequences.awstools.regions._
import ohnosequences.awstools.ec2._

// import java.util.Date
import scala.util.Try
import scala.collection.JavaConversions._


case class AutoScalingGroupSize(
  min: Int,
  desired: Int,
  max: Int
)


case class ScalaAutoScalingClient(val asJava: AmazonAutoScaling) { autoscaling =>

  /* ### Launch configuration operations */

  def getLaunchConfig(name: String): Try[model.LaunchConfiguration] = Try {
    val response = asJava.describeLaunchConfigurations(
      new DescribeLaunchConfigurationsRequest()
        .withLaunchConfigurationNames(name)
        .withMaxRecords(1)
    )
    response.getLaunchConfigurations
      .headOption.getOrElse(
        throw new java.util.NoSuchElementException(s"Launch configuration with the name [${name}] doesn't exist")
      )
  }

  def createOrGetLaunchConfig(
    name: String,
    purchaseModel: AnyPurchaseModel,
    launchSpecs: AnyLaunchSpecs
  ): Try[model.LaunchConfiguration] = {

    val request = {
      val r1 =new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(name)
        .withImageId(launchSpecs.instanceSpecs.ami.id)
        .withInstanceType(launchSpecs.instanceSpecs.instanceType.toString)
        .withUserData(base64encode(launchSpecs.userData))
        .withKeyName(launchSpecs.keyName)
        .withSecurityGroups(launchSpecs.securityGroups)
        .withInstanceMonitoring(new InstanceMonitoring().withEnabled(launchSpecs.instanceMonitoring))
        .withBlockDeviceMappings(
          launchSpecs.deviceMapping.map{ case (key, value) =>
            new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
          }
        )

      val r2 = purchaseModel match {
        case OnDemand => r1
        case pm @ Spot(_, _) => {
          val price = pm.getPrice(launchSpecs.instanceSpecs.instanceType)
          r1.withSpotPrice(price.toString)
        }
      }

      launchSpecs.instanceProfile match {
        case None => r2
        case Some(name) => r2.withIamInstanceProfile(name)
      }
    }

    Try {
      // NOTE: response doesn't carry any information
      asJava.createLaunchConfiguration(request)
    }.recoverWith {
      case _: AlreadyExistsException => scala.util.Success(())
    }.flatMap { _ =>
      getLaunchConfig(name)
    }
  }

  def deleteLaunchConfig(name: String): Try[Unit] = Try {
    asJava.deleteLaunchConfiguration(
      new DeleteLaunchConfigurationRequest()
        .withLaunchConfigurationName(name)
    )
  }

  // TODO: list all launch configs

  /* ### Auto Scaling groups operations */

  def getGroup(name: String): Try[model.AutoScalingGroup] = Try {
    val response = asJava.describeAutoScalingGroups(
      new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(name)
    )

    response.getAutoScalingGroups
      .headOption.getOrElse(
        throw new java.util.NoSuchElementException(s"Auto Scaling group with the name [${name}] doesn't exist")
      )
  }

  def createOrGetGroup(
    name: String,
    launchConfigName: String,
    size: AutoScalingGroupSize,
    zones: List[String] = List()
  ): Try[model.AutoScalingGroup] = {
    val request = {
      val r = new CreateAutoScalingGroupRequest()
        .withAutoScalingGroupName(name)
        .withLaunchConfigurationName(launchConfigName)
        .withMinSize(size.min)
        .withDesiredCapacity(size.desired)
        .withMaxSize(size.max)

      if (zones.isEmpty) r
      else r.withAvailabilityZones(zones)
    }

    Try {
      // NOTE: response doesn't carry any information
      asJava.createAutoScalingGroup(request)
    }.recoverWith {
      case _: AlreadyExistsException => scala.util.Success(())
    }.flatMap { _ =>
      getGroup(name)
    }
  }

  def deleteGroup(
    name: String,
    //  NOTE: with force `true` deletes the group along with all instances associated with the group, without waiting for all instances to be terminated
    force: Boolean = true
  ): Try[Unit] = Try {
    asJava.deleteAutoScalingGroup(
      new DeleteAutoScalingGroupRequest()
        .withAutoScalingGroupName(name)
        .withForceDelete(force)
    )
  }

  // TODO: list all groups

  // def fixAutoScalingGroupUserData(group: AutoScalingGroup, fixedUserData: String): AutoScalingGroup = {
  //   val lc = group.launchConfiguration
  //   val ls = lc.launchSpecs
  //
  //   group.copy( launchConfiguration =
  //     lc.copy( launchSpecs =
  //       LaunchSpecs(ls.instanceSpecs)(
  //         ls.keyName,
  //         userData = fixedUserData,
  //         ls.instanceProfile,
  //         ls.securityGroups,
  //         ls.instanceMonitoring,
  //         ls.deviceMapping
  //       )
  //     )
  //   )
  // }

  // def setDesiredCapacity(group: ohnosequences.awstools.autoscaling.AutoScalingGroup, capacity: Int) {
  //   asJava.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
  //     .withAutoScalingGroupName(group.name)
  //     .withDesiredCapacity(capacity)
  //   )
  // }
  //
  // def getCreatedTimeTry(name: String): Try[Date] = {
  //   Try {
  //     asJava.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
  //       .withAutoScalingGroupNames(name)
  //     ).getAutoScalingGroups.head.getCreatedTime
  //   }
  // }


  /* ### Tags operations */

  // def describeTags(name: String): List[InstanceTag] = {
  //   asJava.describeTags(new DescribeTagsRequest()
  //     .withFilters(
  //       new Filter()
  //         .withName("auto-scaling-group")
  //         .withValues(name)
  //     )
  //   ).getTags.toList.map { tagDescription =>
  //     InstanceTag(tagDescription.getKey, tagDescription.getValue)
  //   }
  // }
  //
  // def getTagValue(groupName: String, tagName: String): Option[String] = {
  //   describeTags(groupName).find(_.name.equals(tagName)).map(_.value)
  // }
  //
  // def createTags(name: String, tags: InstanceTag*) {
  //   val asTags = tags.map { tag =>
  //     new Tag().withKey(tag.name).withValue(tag.value).withResourceId(name).withPropagateAtLaunch(true).withResourceType("auto-scaling-group")
  //   }
  //   asJava.createOrUpdateTags(new CreateOrUpdateTagsRequest()
  //     .withTags(asTags)
  //   )
  // }


}
