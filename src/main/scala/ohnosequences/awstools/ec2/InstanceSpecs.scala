package ohnosequences.awstools.ec2

import ohnosequences.awstools.ec2.ami._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._


trait AnyInstanceSpecs {

  type AMI <: AnyLinuxAMI
  val  ami: AMI

  type InstanceType <: AnyInstanceType
  val  instanceType: InstanceType

  // val instanceTypeCompatibleWithAMI:

  // poorly-typed params:
  val keyName: String
  val userData: String
  val instanceProfile: Option[String]
  val securityGroups: List[String]
  val instanceMonitoring: Boolean
  val deviceMapping: Map[String, String]
}


case class InstanceSpecs[
  T <: AnyInstanceType,
  A <: AnyLinuxAMI
](val instanceType: T,
  val amiId: A,
  val keyName: String,
  val userData: String = "",
  val instanceProfile: Option[String] = None,
  val securityGroups: List[String] = List(),
  val instanceMonitoring: Boolean = false,
  val deviceMapping: Map[String, String] = Map[String, String]()
) extends AnyInstanceSpecs


case object InstanceSpecs {

  implicit def getLaunchSpecs(specs: AnyInstanceSpecs): amzn.ec2.model.LaunchSpecification = {
    val ls = new amzn.ec2.model.LaunchSpecification()
      .withSecurityGroups(specs.securityGroups)
      .withInstanceType(specs.instanceType.toAWS)
      .withImageId(specs.ami.id)
      .withKeyName(specs.keyName)
      .withMonitoringEnabled(specs.instanceMonitoring)
      .withBlockDeviceMappings(specs.deviceMapping.map{ case (key, value) =>
        new amzn.ec2.model.BlockDeviceMapping()
          .withDeviceName(key)
          .withVirtualName(value)
      })
      .withUserData(base64encode(specs.userData))

    specs.instanceProfile match {
      case Some(name) => ls.withIamInstanceProfile(new amzn.ec2.model.IamInstanceProfileSpecification().withName(name))
      case None => ls
    }
  }
}
