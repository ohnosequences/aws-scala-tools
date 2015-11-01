package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._
import java.util.Date

trait AnyAutoScalingGroup {
  val name: String

  type LaunchConfiguration <: AnyLaunchConfiguration
  val  launchConfiguration: LaunchConfiguration

  val minSize: Int
  val desiredCapacity: Int
  val maxSize: Int

  val availabilityZones: List[String]
}

case class AutoScalingGroup[
  LC <: AnyLaunchConfiguration
](val launchConfiguration: LC
)(val name: String = "",
  val minSize: Int,
  val desiredCapacity: Int,
  val maxSize: Int,
  // FIXME: o_O
  val availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
)


// object LaunchConfiguration {
//
//   def fromAWS(launchConfiguration: amzn.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {
//
//     LaunchConfiguration(
//       name = launchConfiguration.getLaunchConfigurationName,
//
//       purchaseModel = Utils.stringToOption(launchConfiguration.getSpotPrice) match {
//         case None => OnDemand
//         case Some(price) => Spot(price.toDouble)
//       },
//       launchSpecs = LaunchSpecs(
//         instanceType = InstanceType.fromName(launchConfiguration.getInstanceType),
//         amiId = launchConfiguration.getImageId,
//         keyName = launchConfiguration.getKeyName,
//         securityGroups = launchConfiguration.getSecurityGroups.toList,
//         deviceMapping = launchConfiguration.getBlockDeviceMappings.map(m => (m.getDeviceName, m.getVirtualName)).toMap,
//         userData = launchConfiguration.getUserData,
//         instanceProfile =  Utils.stringToOption(launchConfiguration.getIamInstanceProfile),
//         instanceMonitoring = launchConfiguration.getInstanceMonitoring.isEnabled
//       )
//     )
//   }
// }

// object AutoScalingGroup {
//   def fromAWS(autoScalingGroup: amzn.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
//     autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
//       case None => None;//since launch configuration deleted this autoscaling group will be deleted soon
//       case Some(launchConfiguration) => Some(AutoScalingGroup(
//         name = autoScalingGroup.getAutoScalingGroupName,
//         launchingConfiguration = launchConfiguration,
//         minSize = autoScalingGroup.getMinSize,
//         maxSize = autoScalingGroup.getMaxSize,
//         desiredCapacity = autoScalingGroup.getDesiredCapacity
//       ))
//     }
//   }
// }
