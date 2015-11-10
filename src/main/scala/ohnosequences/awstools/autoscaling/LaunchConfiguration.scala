package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._

case class LaunchConfiguration(
  val name: String,
  val purchaseModel: AnyPurchaseModel,
  val launchSpecs: AnyLaunchSpecs
) //extends AnyLaunchConfiguration


case object LaunchConfiguration {

  // NOTE: this is an awful conversion
  // FIXME: remove it
  @deprecated("Don't convert java sdk types to the scala ones", since = "v0.15.0")
  def fromAWS(launchConfiguration: amzn.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {

    LaunchConfiguration(
      name = launchConfiguration.getLaunchConfigurationName,
      purchaseModel = stringToOption(launchConfiguration.getSpotPrice) match {
        case None => OnDemand
        case Some(price) => Spot(price.toDouble)
      },
      launchSpecs = LaunchSpecs(
        new AnyInstanceSpecs {
          type InstanceType = AnyInstanceType
          val instanceType: AnyInstanceType =
            InstanceType.fromName(launchConfiguration.getInstanceType)

          type AMI = AnyAMI
          val ami = new AnyAMI {
            val id = launchConfiguration.getImageId
          }
        }
      )(keyName = launchConfiguration.getKeyName,
        securityGroups = launchConfiguration.getSecurityGroups.toList,
        deviceMapping = launchConfiguration.getBlockDeviceMappings.map(m => (m.getDeviceName, m.getVirtualName)).toMap,
        userData = launchConfiguration.getUserData,
        instanceProfile = stringToOption(launchConfiguration.getIamInstanceProfile),
        instanceMonitoring = launchConfiguration.getInstanceMonitoring.isEnabled
      )
    )
  }
}
