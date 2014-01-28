package ohnosequences.awstools.ec2

import org.junit.Test
import org.junit.Assert._

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

import ohnosequences.awstools.ec2.InstanceType._

class InstanceTypeTests {

  @Test
  def toJavaTypeTest() {
    assertEquals(toJavaInstanceType(t1_micro),    JavaInstanceType.T1Micro)
    assertEquals(toJavaInstanceType(m1_small),    JavaInstanceType.M1Small)
    assertEquals(toJavaInstanceType(m1_medium),   JavaInstanceType.M1Medium)
    assertEquals(toJavaInstanceType(m1_large),    JavaInstanceType.M1Large)
    assertEquals(toJavaInstanceType(m1_xlarge),   JavaInstanceType.M1Xlarge)
    assertEquals(toJavaInstanceType(m3_xlarge),   JavaInstanceType.M3Xlarge)
    assertEquals(toJavaInstanceType(m3_2xlarge),  JavaInstanceType.M32xlarge)
    assertEquals(toJavaInstanceType(m2_xlarge),   JavaInstanceType.M2Xlarge)
    assertEquals(toJavaInstanceType(m2_2xlarge),  JavaInstanceType.M22xlarge)
    assertEquals(toJavaInstanceType(m2_4xlarge),  JavaInstanceType.M24xlarge)
    assertEquals(toJavaInstanceType(cr1_8xlarge), JavaInstanceType.Cr18xlarge)
    assertEquals(toJavaInstanceType(i2_xlarge),   JavaInstanceType.I2Xlarge)
    assertEquals(toJavaInstanceType(i2_2xlarge),  JavaInstanceType.I22xlarge)
    assertEquals(toJavaInstanceType(i2_4xlarge),  JavaInstanceType.I24xlarge)
    assertEquals(toJavaInstanceType(i2_8xlarge),  JavaInstanceType.I28xlarge)
    assertEquals(toJavaInstanceType(hi1_4xlarge), JavaInstanceType.Hi14xlarge)
    assertEquals(toJavaInstanceType(hs1_8xlarge), JavaInstanceType.Hs18xlarge)
    assertEquals(toJavaInstanceType(c1_medium),   JavaInstanceType.C1Medium)
    assertEquals(toJavaInstanceType(c1_xlarge),   JavaInstanceType.C1Xlarge)
    assertEquals(toJavaInstanceType(c3_large),    JavaInstanceType.C3Large)
    assertEquals(toJavaInstanceType(c3_xlarge),   JavaInstanceType.C3Xlarge)
    assertEquals(toJavaInstanceType(c3_2xlarge),  JavaInstanceType.C32xlarge)
    assertEquals(toJavaInstanceType(c3_4xlarge),  JavaInstanceType.C34xlarge)
    assertEquals(toJavaInstanceType(c3_8xlarge),  JavaInstanceType.C38xlarge)
    assertEquals(toJavaInstanceType(cc1_4xlarge), JavaInstanceType.Cc14xlarge)
    assertEquals(toJavaInstanceType(cc2_8xlarge), JavaInstanceType.Cc28xlarge)
    assertEquals(toJavaInstanceType(g2_2xlarge),  JavaInstanceType.G22xlarge)
    assertEquals(toJavaInstanceType(cg1_4xlarge), JavaInstanceType.Cg14xlarge)
  }

}
