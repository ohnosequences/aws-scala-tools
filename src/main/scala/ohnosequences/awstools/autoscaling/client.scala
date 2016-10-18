package ohnosequences.awstools.autoscaling

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.{ AmazonAutoScaling, AmazonAutoScalingClient }
import com.amazonaws.services.autoscaling.model._
import ohnosequences.awstools.ec2._
import scala.collection.JavaConversions._
import scala.util.Try


case class AutoScalingGroupSize(
  min: Int,
  desired: Int,
  max: Int
)


case class ScalaAutoScalingClient(val asJava: AmazonAutoScaling) { autoscaling =>

  /* ### Launch configuration operations */

  def getLaunchConfig(name: String): Try[LaunchConfiguration] = Try {
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
  ): Try[LaunchConfiguration] = {

    val request = {
      val r1 =new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(name)
        .withImageId(launchSpecs.instanceSpecs.ami.id)
        .withInstanceType(launchSpecs.instanceSpecs.instanceType.toString)
        // TODO: review this base64encode
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

  def getGroup(name: String): Try[AutoScalingGroup] = Try {
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
  ): Try[AutoScalingGroup] = {
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

  def setDesiredCapacity(groupName: String, capacity: Int): Try[Unit] = Try {
    asJava.setDesiredCapacity(
      new SetDesiredCapacityRequest()
        .withAutoScalingGroupName(groupName)
        .withDesiredCapacity(capacity)
    )
  }


  /* ### Tags operations */

  /* This method returns all tags retrieved with the given filters */
  def tags(filters: AutoScalingTagFilter*): Try[Seq[Tag]] = Try {

    // FIXME: rotate the token to get all tags
    asJava.describeTags(
      new DescribeTagsRequest().withFilters(filters.map(_.asJava))
    ).getTags
      // NOTE: Amazon returns a `TagDescription` which is **exactly** the same as just `Tag`, so we just convert the former to the latter
      .map(tagDescriptionToTag)
  }

  /* Same as the `tags` method, but with the result as a simple `String` map */
  def tagsMap(filters: AutoScalingTagFilter*): Try[Map[String, String]] = {
    tags(filters: _*).map {
      _.map { tag =>
        tag.getKey -> tag.getValue
      }.toMap
    }
  }

  /* Tries to get the value of the tag with the given key. If a tag with this key doesn't exist, it will fail with the `NoSuchElementException` */
  def tagValue(groupName: String, tagKey: String): Try[String] = {
    tagsMap(
      ByGroupNames(groupName),
      ByTagKeys(tagKey)
    ).map {
      _.apply(tagKey)
    }
  }

  /* This method sets/updates tag values for a given Auto Scaling group or creates them if they don't exist */
  def setTags(
    groupName: String,
    tags: Map[String, String],
    propagateAtLaunch: Boolean = true
  ): Try[Unit] = Try {

    val autoscalingTags = tags.map { case (key, value) =>
      new Tag()
        .withResourceType("auto-scaling-group").withResourceId(groupName)
        .withKey(key).withValue(value)
        .withPropagateAtLaunch(propagateAtLaunch)
    }

    asJava.createOrUpdateTags(
      new CreateOrUpdateTagsRequest().withTags(autoscalingTags)
    )
  }

}
