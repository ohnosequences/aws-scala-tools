package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }

sealed trait AnyPurchaseModel

case object OnDemand extends AnyPurchaseModel

sealed trait AnySpotModel extends AnyPurchaseModel {
  val maxPrice: Option[Double]

  // variation from the current spot price
  val delta: Option[Double]

  def price(ec2: EC2, instanceType: AnyInstanceType): Double = {
    math.max(
      ec2.getCurrentSpotPrice(instanceType) + delta.getOrElse(0D),
      maxPrice.getOrElse(0D)
    )
  }
}

case class Spot(
  val maxPrice: Option[Double] = None,
  val delta: Option[Double] = Some(0.001D)
) extends AnySpotModel
