package ohnosequences.awstools.test

import ohnosequences.awstools._, autoscaling._
import com.amazonaws.services.autoscaling.AmazonAutoScaling

class AutoScaling extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val autoscaling: AmazonAutoScaling = AutoScalingClient()


  // override def beforeAll() = {
  // }
  //
  // override def afterAll() = {
  // }

}
