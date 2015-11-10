package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }

sealed trait AnyPurchaseModel {}

case object OnDemand extends AnyPurchaseModel

case class Spot(val price: Double) extends AnyPurchaseModel

// TODO: SpotAuto with absolute limit and configurable delta
case object SpotAuto extends AnyPurchaseModel {

  def getCurrentPrice(ec2: EC2, instanceType: AnyInstanceType): Double = {
    ec2.getCurrentSpotPrice(instanceType) + 0.001
  }
}
