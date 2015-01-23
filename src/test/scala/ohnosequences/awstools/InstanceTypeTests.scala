package ohnosequences.awstools.ec2

import org.junit.Test
import org.junit.Assert._

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

import ohnosequences.awstools.ec2.InstanceType._

class InstanceTypeTests {

  @Test
  def toJavaTypeTest() {

    // General purpose
    assert{ t2_micro.toAWS   == JavaInstanceType.fromValue("t2.micro") }
    assert{ t2_small.toAWS   == JavaInstanceType.fromValue("t2.small") }
    assert{ t2_medium.toAWS  == JavaInstanceType.fromValue("t2.medium") }
    assert{ m3_medium.toAWS  == JavaInstanceType.fromValue("m3.medium") }
    assert{ m3_large.toAWS   == JavaInstanceType.fromValue("m3.large") }
    assert{ m3_xlarge.toAWS  == JavaInstanceType.fromValue("m3.xlarge") }
    assert{ m3_2xlarge.toAWS == JavaInstanceType.fromValue("m3.2xlarge") }
    // Compute optimized
    assert{ c3_large.toAWS   == JavaInstanceType.fromValue("c3.large") }
    assert{ c3_xlarge.toAWS  == JavaInstanceType.fromValue("c3.xlarge") }
    assert{ c3_2xlarge.toAWS == JavaInstanceType.fromValue("c3.2xlarge") }
    assert{ c3_4xlarge.toAWS == JavaInstanceType.fromValue("c3.4xlarge") }
    assert{ c3_8xlarge.toAWS == JavaInstanceType.fromValue("c3.8xlarge") }
    // Memory optimized
    assert{ r3_large.toAWS   == JavaInstanceType.fromValue("r3.large") }
    assert{ r3_xlarge.toAWS  == JavaInstanceType.fromValue("r3.xlarge") }
    assert{ r3_2xlarge.toAWS == JavaInstanceType.fromValue("r3.2xlarge") }
    assert{ r3_4xlarge.toAWS == JavaInstanceType.fromValue("r3.4xlarge") }
    assert{ r3_8xlarge.toAWS == JavaInstanceType.fromValue("r3.8xlarge") }
    // Storage optimized
    assert{ i2_xlarge.toAWS   == JavaInstanceType.fromValue("i2.xlarge") }
    assert{ i2_2xlarge.toAWS  == JavaInstanceType.fromValue("i2.2xlarge") }
    assert{ i2_4xlarge.toAWS  == JavaInstanceType.fromValue("i2.4xlarge") }
    assert{ i2_8xlarge.toAWS  == JavaInstanceType.fromValue("i2.8xlarge") }
    assert{ hs1_8xlarge.toAWS == JavaInstanceType.fromValue("hs1.8xlarge") }
    // GPU instances
    assert{ g2_2xlarge.toAWS == JavaInstanceType.fromValue("g2.2xlarge") }

    // Previous Generation Instances //

    // General purpose
    assert{ m1_small.toAWS  == JavaInstanceType.fromValue("m1.small") }
    assert{ m1_medium.toAWS == JavaInstanceType.fromValue("m1.medium") }
    assert{ m1_large.toAWS  == JavaInstanceType.fromValue("m1.large") }
    assert{ m1_xlarge.toAWS == JavaInstanceType.fromValue("m1.xlarge") }
    // Compute optimized
    assert{ c1_medium.toAWS   == JavaInstanceType.fromValue("c1.medium") }
    assert{ c1_xlarge.toAWS   == JavaInstanceType.fromValue("c1.xlarge") }
    assert{ cc2_8xlarge.toAWS == JavaInstanceType.fromValue("cc2.8xlarge") }
    // Memory optimized
    assert{ m2_xlarge.toAWS   == JavaInstanceType.fromValue("m2.xlarge") }
    assert{ m2_2xlarge.toAWS  == JavaInstanceType.fromValue("m2.2xlarge") }
    assert{ m2_4xlarge.toAWS  == JavaInstanceType.fromValue("m2.4xlarge") }
    assert{ cr1_8xlarge.toAWS == JavaInstanceType.fromValue("cr1.8xlarge") }
    // Storage optimized
    assert{ hi1_4xlarge.toAWS == JavaInstanceType.fromValue("hi1.4xlarge") }
    // GPU instances
    assert{ cg1_4xlarge.toAWS == JavaInstanceType.fromValue("cg1.4xlarge") }
    // Micro instances
    assert{ t1_micro.toAWS == JavaInstanceType.fromValue("t1.micro") }

  }

}
