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
    // NOTE: this returns a String, thus the other way round conversion
    def getRegion(): String = region.getName
  }
  implicit def RegionsToProvider(region: Regions): AwsRegionProvider = RegionToProvider(region)
  implicit def ProviderToRegion(provider: AwsRegionProvider): Region = Regions.fromName(provider.getRegion)


  /*  ### Geographical aliales */
  sealed abstract class RegionAlias(val regions: Regions)

  // NOTE: thanks to these conversions, you can use these aliases as any of the SDK's three region types
  implicit def RegionAliasToRegions (alias: RegionAlias): Regions = alias.region
  implicit def RegionAliasToRegion  (alias: RegionAlias): Region  = alias.region
  implicit def RegionAliasToProvider(alias: RegionAlias): AwsRegionProvider = alias.region

  /* - Asia Pacific */
  case object Tokyo              extends RegionAlias(Regions.AP_NORTHEAST_1)
  case object Seoul              extends RegionAlias(Regions.AP_NORTHEAST_2)
  case object Mumbai             extends RegionAlias(Regions.AP_SOUTH_1)
  case object Singapore          extends RegionAlias(Regions.AP_SOUTHEAST_1)
  case object Sydney             extends RegionAlias(Regions.AP_SOUTHEAST_2)
  /* - China */
  case object Beijing            extends RegionAlias(Regions.CN_NORTH_1)
  /* - Europe */
  case object Frankfurt          extends RegionAlias(Regions.EU_CENTRAL_1)
  case object Ireland            extends RegionAlias(Regions.EU_WEST_1)
  /* - Somewhere in CIA */
  case object GovCloud           extends RegionAlias(Regions.GovCloud)
  /* - South America */
  case object SaoPaulo           extends RegionAlias(Regions.SA_EAST_1)
  /* - US East */
  case object NorthernVirginia   extends RegionAlias(Regions.US_EAST_1)
  // TODO: update sdk version:
  case object Ohio               extends RegionAlias(Regions.US_EAST_2)
  /* - US West */
  case object NorthernCalifornia extends RegionAlias(Regions.US_WEST_1)
  case object Oregon             extends RegionAlias(Regions.US_WEST_2)
}
