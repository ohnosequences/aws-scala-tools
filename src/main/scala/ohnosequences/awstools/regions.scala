package ohnosequences.awstools

import com.amazonaws.regions._

package object regions {

  /* Adding SDK types to the scope without additional imports: */
  type Regions = com.amazonaws.regions.Regions
  type Region  = com.amazonaws.regions.Region
  type AwsRegionProvider = com.amazonaws.regions.AwsRegionProvider
  type DefaultAwsRegionProviderChain = com.amazonaws.regions.DefaultAwsRegionProviderChain

  /* Converting enum values to the `Region` type: */
  implicit def RegionsToRegion(regions: Regions): Region = Region.getRegion(regions)

  /* Converting explicit region to an `AwsRegionProvider` and another way round */
  implicit def RegionToProvider(region: Region): AwsRegionProvider = new AwsRegionProvider {
    // NOTE: this returns a String, thus the following conversion
    def getRegion(): String = region.getName
  }
  implicit def ProviderToRegion(provider: AwsRegionProvider): Region = Regions.fromName(provider.getRegion)


  /*  ### Geographical aliales */

  /* - Asia Pacific */
  val Tokyo              = Regions.AP_NORTHEAST_1
  val Seoul              = Regions.AP_NORTHEAST_2
  val Mumbai             = Regions.AP_SOUTH_1
  val Singapore          = Regions.AP_SOUTHEAST_1
  val Sydney             = Regions.AP_SOUTHEAST_2
  /* - China */
  val Beijing            = Regions.CN_NORTH_1
  /* - Europe */
  val Frankfurt          = Regions.EU_CENTRAL_1
  val Ireland            = Regions.EU_WEST_1
  /* - Somewhere in CIA */
  val GovCloud           = Regions.GovCloud
  /* - South America */
  val SaoPaulo           = Regions.SA_EAST_1
  /* - US East */
  val NorthernVirginia   = Regions.US_EAST_1
  // TODO: update sdk version:
  val Ohio               = Regions.US_EAST_2
  /* - US West */
  val NorthernCalifornia = Regions.US_WEST_1
  val Oregon             = Regions.US_WEST_2
}
