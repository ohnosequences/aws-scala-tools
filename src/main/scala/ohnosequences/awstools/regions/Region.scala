package ohnosequences.awstools.regions

import com.amazonaws.regions.{Regions => JavaRegions}
import com.amazonaws.regions.{Region => JavaRegion}

sealed abstract class Region(val name: String) {
  override def toString = name

  def toAWSRegion = Region.toJavaRegions(this)
}

object Region {

  // The same names as in the Java AWS SDK
  case object AP_NORTHEAST_1 extends Region("ap-northeast-1") // Tokyo
  case object AP_SOUTHEAST_1 extends Region("ap-southeast-1") // Singapore
  case object AP_SOUTHEAST_2 extends Region("ap-southeast-2") // Sydney
  case object EU_WEST_1      extends Region("eu-west-1")      // Ireland
  case object SA_EAST_1      extends Region("sa-east-1")      // São Paulo
  case object US_EAST_1      extends Region("us-east-1")      // Northern Virginia
  case object US_WEST_1      extends Region("us-west-1")      // Northern California
  case object US_WEST_2      extends Region("us-west-2")      // Oregon
  case object GovCloud       extends Region("us-gov-west-1")  // Secret cloud for CIA

  implicit def toJavaRegions(r: Region): JavaRegions =
    JavaRegions.fromName(r.name)

  implicit def toJavaRegion(r: Region): JavaRegion =
    JavaRegion.getRegion(toJavaRegions(r))

  // Nice geographical synonims:
  val Tokyo              = AP_NORTHEAST_1
  val Singapore          = AP_SOUTHEAST_1
  val Sydney             = AP_SOUTHEAST_2
  val Ireland            = EU_WEST_1
  val SaoPaulo           = SA_EAST_1
  val NorthernVirginia   = US_EAST_1
  val NorthernCalifornia = US_WEST_1
  val Oregon             = US_WEST_2
}
