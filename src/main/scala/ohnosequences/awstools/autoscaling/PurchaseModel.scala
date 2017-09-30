package ohnosequences.awstools.autoscaling

case class PurchaseModel(val maxPrice: Option[Double]) {

  val isSpot = maxPrice.nonEmpty
}

case object PurchaseModel {

  def onDemand:          PurchaseModel = PurchaseModel(None)
  def spot(max: Double): PurchaseModel = PurchaseModel(Some(max))
}

// NOTE: you can set maximum price depending on the current spot price:
// PurchaseModel.spot(ec2.getCurrentSpotPrice(instanceType) + 0.001)
