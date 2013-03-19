package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2.{InstanceType, InstanceSpecs}
import scala.collection.JavaConversions._

case class AutoScalingGroup(
  name: String,
  launchingConfiguration: String,
  minSize: Int,
  maxSize: Int,
  desiredCapacity: Int,
  availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
)

case class LaunchConfiguration(
  name: String,
  spotPrice: Double,
  instanceSpecs: InstanceSpecs
)

object LaunchConfiguration {
  def fromAWS(launchConfiguration: com.amazonaws.services.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {
    LaunchConfiguration(
      name = launchConfiguration.getLaunchConfigurationName,
      spotPrice = launchConfiguration.getSpotPrice.toDouble,
      instanceSpecs = InstanceSpecs(
        instanceType = InstanceType.fromName(launchConfiguration.getInstanceType),
        amiId = launchConfiguration.getImageId,
        keyName = launchConfiguration.getKeyName,
        securityGroups = launchConfiguration.getSecurityGroups.toList,
        userData = launchConfiguration.getUserData
      )
    )
  }
}

object AutoScalingGroup {
  def fromAWS(autoScalingGroup: com.amazonaws.services.autoscaling.model.AutoScalingGroup): AutoScalingGroup = {
    AutoScalingGroup(
      name = autoScalingGroup.getAutoScalingGroupName,
      launchingConfiguration = autoScalingGroup.getLaunchConfigurationName,
      minSize = autoScalingGroup.getMinSize,
      maxSize = autoScalingGroup.getMaxSize,
      desiredCapacity = autoScalingGroup.getDesiredCapacity
    )
  }
}
