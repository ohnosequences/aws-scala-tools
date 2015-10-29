package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._
import java.util.Date

case class AutoScalingGroup[
  IS <: AnyInstanceSpecs
](name: String = "",
  launchingConfiguration: LaunchConfiguration[IS],
  minSize: Int,
  maxSize: Int,
  desiredCapacity: Int,
  availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
)

sealed abstract class PurchaseModel

case object OnDemand extends PurchaseModel

case object SpotAuto extends PurchaseModel {
  def getCurrentPrice(ec2: EC2, instanceType: AnyInstanceType): Double = {
    ec2.getCurrentSpotPrice(instanceType) + 0.001
  }
}

case class Spot(price: Double) extends PurchaseModel


trait AnyLaunchConfiguration

case class LaunchConfiguration[I <: AnyInstanceSpecs](
  name: String = "",
  purchaseModel: PurchaseModel,
  launchSpecs: LaunchSpecs[I]
) extends AnyLaunchConfiguration


object LaunchConfiguration {

  def fromAWS(launchConfiguration: amzn.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {

    LaunchConfiguration(
      name = launchConfiguration.getLaunchConfigurationName,

      purchaseModel = Utils.stringToOption(launchConfiguration.getSpotPrice) match {
        case None => OnDemand
        case Some(price) => Spot(price.toDouble)
      },
      launchSpecs = LaunchSpecs(
        instanceType = InstanceType.fromName(launchConfiguration.getInstanceType),
        amiId = launchConfiguration.getImageId,
        keyName = launchConfiguration.getKeyName,
        securityGroups = launchConfiguration.getSecurityGroups.toList,
        deviceMapping = launchConfiguration.getBlockDeviceMappings.map(m => (m.getDeviceName, m.getVirtualName)).toMap,
        userData = launchConfiguration.getUserData,
        instanceProfile =  Utils.stringToOption(launchConfiguration.getIamInstanceProfile),
        instanceMonitoring = launchConfiguration.getInstanceMonitoring.isEnabled
      )
    )
  }
}

object AutoScalingGroup {
  def fromAWS(autoScalingGroup: amzn.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
    autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
      case None => None;//since launch configuration deleted this autoscaling group will be deleted soon
      case Some(launchConfiguration) => Some(AutoScalingGroup(
        name = autoScalingGroup.getAutoScalingGroupName,
        launchingConfiguration = launchConfiguration,
        minSize = autoScalingGroup.getMinSize,
        maxSize = autoScalingGroup.getMaxSize,
        desiredCapacity = autoScalingGroup.getDesiredCapacity

      ))
    }
  }
}
