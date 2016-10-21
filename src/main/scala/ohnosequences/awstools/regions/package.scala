package ohnosequences.awstools

import com.amazonaws.regions._

package object regions {

  /* Adding SDK types to the scope without SDK imports: */
  type Regions = com.amazonaws.regions.Regions
  type Region  = com.amazonaws.regions.Region
  type AwsRegionProvider = com.amazonaws.regions.AwsRegionProvider
  type DefaultAwsRegionProviderChain = com.amazonaws.regions.DefaultAwsRegionProviderChain

  /* ### Implicits */

  /* - `Regions` enum → `Region` and `AwsRegionProvider` */
  implicit def RegionsToRegion(regions: Regions): Region = Region.getRegion(regions)
  implicit def RegionsToProvider(region: Regions): AwsRegionProvider = RegionToProvider(region)

  /* - `Region` type ⇄ `AwsRegionProvider` */
  implicit def RegionToProvider(region: Region): AwsRegionProvider = new AwsRegionProvider {
    override def getRegion(): String = region.getName
  }
  implicit def ProviderToRegion(provider: AwsRegionProvider): Region = Regions.fromName(provider.getRegion)

  /* - `RegionAlias` → each of the other three */
  implicit def RegionAliasToRegions (alias: RegionAlias): Regions = alias.region
  implicit def RegionAliasToRegion  (alias: RegionAlias): Region  = alias.region
  implicit def RegionAliasToProvider(alias: RegionAlias): AwsRegionProvider = alias.region

}
