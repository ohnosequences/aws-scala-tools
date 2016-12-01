package ohnosequences.awstools.regions

import com.amazonaws.regions._

/* ## Geographical aliales

  This sealed class reflects the `Regions` from the SDK, but allows us to have
  - convenient aliases based on the geographical locations
  - precise type for each region and dispatch on it in implicit resolution (see AMIs code)

  This type is also implicitly converted to `Region`, `Regions` and `AwsRegionProvider` types, so you can use it anywhere those types are expected (once you've imported `ohnosequences.awstools.regions._`)
*/
sealed abstract class RegionAlias(val region: Regions) {

  override def toString = region.getName
}


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
case object Ohio               extends RegionAlias(Regions.US_EAST_2)
/* - US West */
case object NorthernCalifornia extends RegionAlias(Regions.US_WEST_1)
case object Oregon             extends RegionAlias(Regions.US_WEST_2)
