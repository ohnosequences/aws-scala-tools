package ohnosequences.awstools.test

import com.amazonaws.regions.{ Regions => JavaRegions }

class RegionAliases extends org.scalatest.FunSuite {

  val  javaRegionNames: Set[String] =
    JavaRegions.values.map(_.getName).toSet

  val scalaRegionNames: Set[String] =
    allInstances("ohnosequences.awstools.regions.RegionAlias").map(reflectName)

  test("region aliases correspond to the SDK enum") {

    // scalaRegionNames.foreach { t => info(t) }

    assertResult(Set(), "these types don't exist in the Java SDK") {
      scalaRegionNames diff javaRegionNames
    }

    assertResult(Set(), "these types are defined in the Java SDK, but not in the library") {
      javaRegionNames diff scalaRegionNames
    }
  }

}
