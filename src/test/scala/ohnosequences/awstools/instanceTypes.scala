package ohnosequences.awstools.test

import com.amazonaws.services.ec2.model.{ InstanceType => JavaInstanceType }

class InstanceTypes extends org.scalatest.FunSuite {

  val  javaInstanceTypes: Set[String] =
    JavaInstanceType.values.map(_.toString).toSet

  val scalaInstanceTypes: Set[String] =
    allInstances("ohnosequences.awstools.ec2.InstanceType").map { fqn =>
      fqn.stripPrefix("ohnosequences.awstools.ec2.")
    }

  test("instance types correspond to the SDK enum") {

    // scalaInstanceTypes.foreach { t => info(t) }

    assertResult(Set(), "these types don't exist in the Java SDK") {
      scalaInstanceTypes diff javaInstanceTypes
    }

    assertResult(Set(), "these types are defined in the Java SDK, but not in the library") {
      javaInstanceTypes diff scalaInstanceTypes
    }
  }

}
