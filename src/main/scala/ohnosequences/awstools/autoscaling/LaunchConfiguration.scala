package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._

trait AnyLaunchConfiguration {
  val name: String

  type PurchaseModel <: AnyPurchaseModel
  val  purchaseModel: PurchaseModel

  type LaunchSpecs <: AnyLaunchSpecs
  val  launchSpecs: LaunchSpecs

  // final def toAWSRequest: amzn.autoscaling.model.CreateLaunchConfigurationRequest = {
  //   val lcr = new amzn.autoscaling.model.CreateLaunchConfigurationRequest()
  //     .withLaunchConfigurationName(this.name)
  //     .withImageId(this.launchSpecs.instanceSpecs.ami.id)
  //     .withInstanceType(this.launchSpecs.instanceSpecs.instanceType.toString)
  //     .withUserData(base64encode(this.launchSpecs.userData))
  //     .withKeyName(this.launchSpecs.keyName)
  //     .withSecurityGroups(this.launchSpecs.securityGroups)
  //     .withInstanceMonitoring(new InstanceMonitoring()
  //       .withEnabled(this.launchSpecs.instanceMonitoring)
  //     )
  //     .withBlockDeviceMappings(
  //       this.launchSpecs.deviceMapping.map{ case (key, value) =>
  //         new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
  //       }.toList
  //     )
  //
  //   this.purchaseModel match {
  //     case Spot(price) => lcr.withSpotPrice(price.toString)
  //     case SpotAuto => {
  //       val price = SpotAuto.getCurrentPrice(ec2, this.launchSpecs.instanceType)
  //       lcr.withSpotPrice(price.toString)
  //     }
  //     case OnDemand => lcr
  //   }
  //
  //   lcr = this.launchSpecs.instanceProfile match {
  //     case Some(name) => lcr.withIamInstanceProfile(name)
  //     case None => lcr
  //   }
  //
  //   lcr
  // }
}

case class LaunchConfiguration[
  PM <: AnyPurchaseModel,
  LS <: AnyLaunchSpecs
](val name: String,
  val purchaseModel: PM,
  val launchSpecs: LS
) extends AnyLaunchConfiguration {

  type PurchaseModel = PM
  type LaunchSpecs = LS
}
