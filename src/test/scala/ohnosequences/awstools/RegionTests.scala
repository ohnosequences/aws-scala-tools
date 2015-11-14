// package ohnosequences.awstools.regions
//
// import org.junit.Test
// import org.junit.Assert._
//
// import com.amazonaws.regions.{Regions => JavaRegions}
// import com.amazonaws.regions.{Region => JavaRegion}
//
// import ohnosequences.awstools.regions._
// import ohnosequences.awstools.regions.Region._
//
// class RegionTests {
//
//   @Test
//   def toJavaRegionsTest() {
//     assertEquals(toJavaRegions(Tokyo),              JavaRegions.AP_NORTHEAST_1)
//     assertEquals(toJavaRegions(Singapore),          JavaRegions.AP_SOUTHEAST_1)
//     assertEquals(toJavaRegions(Sydney),             JavaRegions.AP_SOUTHEAST_2)
//     assertEquals(toJavaRegions(Ireland),            JavaRegions.EU_WEST_1)
//     assertEquals(toJavaRegions(SaoPaulo),           JavaRegions.SA_EAST_1)
//     assertEquals(toJavaRegions(NorthernVirginia),   JavaRegions.US_EAST_1)
//     assertEquals(toJavaRegions(NorthernCalifornia), JavaRegions.US_WEST_1)
//     assertEquals(toJavaRegions(Oregon),             JavaRegions.US_WEST_2)
//     assertEquals(toJavaRegions(GovCloud),           JavaRegions.GovCloud)
//   }
//
// }
