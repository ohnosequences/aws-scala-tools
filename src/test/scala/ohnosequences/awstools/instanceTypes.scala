package ohnosequences.awstools.test

import ohnosequences.awstools._, ec2._
import com.amazonaws.services.ec2.model.{ InstanceType => JavaInstanceType }

class InstanceTypes extends org.scalatest.FunSuite {

  val scalaInstanceTypes: Set[String] = allInstancesToString("ohnosequences.awstools.ec2.InstanceType")
  val  javaInstanceTypes: Set[String] = JavaInstanceType.values.map(_.toString).toSet

  test("instance types correspond to the SDK enum") {

    // scalaInstanceTypes.foreach { t => info(t) }

    assertResult(Set(), "these types don't exist in the Java SDK") {
      scalaInstanceTypes diff javaInstanceTypes
    }

    assertResult(Set("cc1.4xlarge"), "these types are defined in the Java SDK, but not in the library") {
      javaInstanceTypes diff scalaInstanceTypes
    }
  }

}
