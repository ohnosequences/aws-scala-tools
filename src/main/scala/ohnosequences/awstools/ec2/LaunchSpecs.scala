package ohnosequences.awstools.ec2

import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._


trait AnyLaunchSpecs {
  type InstanceSpecs <: AnyInstanceSpecs
  val  instanceSpecs: InstanceSpecs

  // poorly-typed params:
  val keyName: String
  val userData: String
  val instanceProfile: Option[String]
  val securityGroups: List[String]
  val instanceMonitoring: Boolean
  val deviceMapping: Map[String, String]

  final def toAWS: amzn.ec2.model.LaunchSpecification = {
    val ls = new amzn.ec2.model.LaunchSpecification()
      .withSecurityGroups(this.securityGroups)
      .withInstanceType(this.instanceSpecs.instanceType.toAWS)
      .withImageId(this.instanceSpecs.ami.id)
      .withKeyName(this.keyName)
      .withMonitoringEnabled(this.instanceMonitoring)
      .withBlockDeviceMappings(this.deviceMapping.map{ case (key, value) =>
        new amzn.ec2.model.BlockDeviceMapping()
          .withDeviceName(key)
          .withVirtualName(value)
      })
      .withUserData(base64encode(this.userData))

    this.instanceProfile match {
      case Some(name) => ls.withIamInstanceProfile(new amzn.ec2.model.IamInstanceProfileSpecification().withName(name))
      case None => ls
    }
  }

}

// case object AnyLaunchSpecs {
//   implicit def getAWSLaunchSpecs(specs: AnyLaunchSpecs):
//     amzn.ec2.model.LaunchSpecification = specs.toAWS
// }

case class LaunchSpecs[
  S <: AnyInstanceSpecs
](val instanceSpecs: S)(
  val keyName: String,
  val userData: String = "",
  val instanceProfile: Option[String] = None,
  val securityGroups: List[String] = List(),
  val instanceMonitoring: Boolean = false,
  val deviceMapping: Map[String, String] = Map[String, String]()
) extends AnyLaunchSpecs {
  type InstanceSpecs = S
}
