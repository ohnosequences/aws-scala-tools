package ohnosequences.awstools.autoscaling

import com.amazonaws.auth._
import com.amazonaws.services.ec2.AmazonEC2
import ohnosequences.awstools.ec2._
import ohnosequences.awstools.regions._
import com.amazonaws.{ services => amzn }

sealed trait AnyPurchaseModel

case object OnDemand extends AnyPurchaseModel

// NOTE: Spot purchase model needs region and credentials to evaluate current spot price (using an EC2 client)
case class Spot(
  val region: Region,
  val credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()
)(val maxPrice: Option[Double] = None,
  // variation from the current spot price
  val delta: Option[Double] = Some(0.001D)
) extends AnyPurchaseModel {

  def getPrice(instanceType: AnyInstanceType): Double = {

    val currentPrice = EC2Client(region, credentials).getCurrentSpotPrice(instanceType)

    math.max(
      maxPrice.getOrElse(0D),
      currentPrice + delta.getOrElse(0D)
    )
  }
}
