package ohnosequences.awstools.test

import ohnosequences.awstools._
import com.amazonaws.services.autoscaling.AmazonAutoScaling

class AutoScaling extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val as: AmazonAutoScaling = autoscaling.defaultClient


  // override def beforeAll() = {
  // }
  //
  // override def afterAll() = {
  // }

}
