package ohnosequences.awstools.autoscaling

import com.amazonaws.services.ec2.AmazonEC2
import ohnosequences.awstools._, ec2._
import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.{ AmazonAutoScaling, AmazonAutoScalingClient }
import com.amazonaws.services.autoscaling.model._
import com.amazonaws.services.autoscaling.waiters.AmazonAutoScalingWaiters
import scala.collection.JavaConversions._
import scala.util.Try


case class AutoScalingGroupSize(
  min: Int,
  desired: Int,
  max: Int
)


case class ScalaAutoScalingClient(val asJava: AmazonAutoScaling) { autoscaling =>

  /* ### Launch configuration operations */

  def getLaunchConfig(configName: String): Try[LaunchConfiguration] = Try {
    val response = autoscaling.asJava.describeLaunchConfigurations(
      new DescribeLaunchConfigurationsRequest()
        .withLaunchConfigurationNames(configName)
        .withMaxRecords(1)
    )
    response.getLaunchConfigurations
      .headOption.getOrElse(
        throw new java.util.NoSuchElementException(s"Launch configuration with the name [${configName}] doesn't exist")
      )
  }

  def createLaunchConfig(
    configName: String,
    purchaseModel: PurchaseModel,
    launchSpecs: AnyLaunchSpecs
  ): Try[Unit] = {

    val request = launchSpecs
      .toCreateLaunchConfigurationRequest
      .withLaunchConfigurationName(configName)

    purchaseModel.maxPrice.fold() { price =>
      request.setSpotPrice(price.toString)
    }

    Try {
      // NOTE: response doesn't carry any information
      autoscaling.asJava.createLaunchConfiguration(request)
    }
  }

  def deleteLaunchConfig(configName: String): Try[Unit] = Try {
    autoscaling.asJava.deleteLaunchConfiguration(
      new DeleteLaunchConfigurationRequest()
        .withLaunchConfigurationName(configName)
    )
  }

  // TODO: list all launch configs

  /* ### Auto Scaling groups operations */

  /* These are just aliases */
  def waitUntil: AmazonAutoScalingWaiters = asJava.waiters

  def getGroup(groupName: String): Try[AutoScalingGroup] = Try {
    val response = autoscaling.asJava.describeAutoScalingGroups(
      new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(groupName)
        .withMaxRecords(1)
    )

    response.getAutoScalingGroups
      .headOption.getOrElse(
        throw new java.util.NoSuchElementException(s"Auto Scaling group with the name [${groupName}] doesn't exist")
      )
  }

  def createGroup(
    groupName: String,
    launchConfigName: String,
    size: AutoScalingGroupSize,
    // NOTE: at the moment I don't see any easy way to get available zones without using an EC2 client (see getAllAvailableZones) and an unset list fails this request
    zones: Set[String]
  ): Try[Unit] = {

    val request = new CreateAutoScalingGroupRequest()
      .withAutoScalingGroupName(groupName)
      .withLaunchConfigurationName(launchConfigName)
      .withMinSize(size.min)
      .withDesiredCapacity(size.desired)
      .withMaxSize(size.max)
      .withAvailabilityZones(zones)

    Try {
      // NOTE: response doesn't carry any information
      autoscaling.asJava.createAutoScalingGroup(request)
    }.map { _ =>
      waitUntil.groupExists.withName(groupName)
    }
  }

  def deleteGroup(
    groupName: String,
    //  NOTE: with force `true` deletes the group along with all instances associated with the group, without waiting for all instances to be terminated
    force: Boolean = true
  ): Try[Unit] = Try {
    autoscaling.asJava.deleteAutoScalingGroup(
      new DeleteAutoScalingGroupRequest()
        .withAutoScalingGroupName(groupName)
        .withForceDelete(force)
    )
  }

  // TODO: list all groups

  def setDesiredCapacity(groupName: String, capacity: Int): Try[Unit] = Try {
    autoscaling.asJava.setDesiredCapacity(
      new SetDesiredCapacityRequest()
        .withAutoScalingGroupName(groupName)
        .withDesiredCapacity(capacity)
    )
  }


  /* ### Tags operations */

  /* This method returns all tags retrieved with the given filters */
  def filterTags(filters: AutoScalingTagFilter*): Try[Stream[Tag]] = Try {

    val request = new DescribeTagsRequest()
      .withFilters(filters.map(_.asJava))

    def fromResponse(response: DescribeTagsResult) = (
      Option(response.getNextToken),
      response.getTags.map(tagDescriptionToTag)
    )

    rotateTokens { token =>
      fromResponse(autoscaling.asJava.describeTags(
        token.fold(request)(request.withNextToken)
      ))
    }
  }

  /* All tags key/value map for a given Auto Scaling group.Note that in contrast to filterTags this method is not lazy. */
  def tagsMap(groupName: String): Try[Map[String, String]] = {
    filterTags(ByGroupNames(groupName)).map {
      _.map { tag =>
        tag.getKey -> tag.getValue
      }.toMap
    }
  }

  /* Tries to get the value of a tag with the given key */
  def tagValue(groupName: String, tagKey: String): Try[String] = {
    filterTags(
      ByGroupNames(groupName),
      ByTagKeys(tagKey)
    ).map {
      _.headOption.getOrElse(
        throw new java.util.NoSuchElementException(s"Tag with the key [${tagKey}] doesn't exist")
      ).getValue
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

    autoscaling.asJava.createOrUpdateTags(
      new CreateOrUpdateTagsRequest().withTags(autoscalingTags)
    )
  }

}
