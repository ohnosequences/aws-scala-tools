package ohnosequences.awstools.autoscaling

import com.amazonaws.auth._
import com.amazonaws.services.ec2.AmazonEC2
import ohnosequences.awstools.ec2._
import ohnosequences.awstools.regions._
import com.amazonaws.{ services => amzn }

case class PurchaseModel(val maxPrice: Option[Double]) {

  val isSpot = maxPrice.nonEmpty
}

case object PurchaseModel {

  def onDemand:          PurchaseModel = PurchaseModel(None)
  def spot(max: Double): PurchaseModel = PurchaseModel(Some(max))
}

// NOTE: you can set maximum price depending on the current spot price:
// PurchaseModel.spot(ec2.getCurrentSpotPrice(instanceType) + 0.001)
