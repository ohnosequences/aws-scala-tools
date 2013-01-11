package ohnosequences.awstools.ec2

import org.junit.Test
import org.junit.Assert._
import com.amazonaws.services.ec2.model.InstanceType._
import com.amazonaws.services.ec2.model.LaunchSpecification

import java.util.Arrays

class EC2Tests {

  @Test
  def instanceSpecs() {
    val specs = InstanceSpecs(instanceType = T1Micro, keyName = "keyName", securityGroups = List("sg1"), amiId = "amiId1")
    assertEquals("amiId1", specs.amiId)
    assertEquals(T1Micro, specs.instanceType)
    assertEquals(List("sg1"), specs.securityGroups)

    val lspecs: LaunchSpecification = specs
    assertEquals("amiId1", lspecs.getImageId)
    assertEquals(T1Micro.toString, lspecs.getInstanceType)
    assertEquals(Arrays.asList("sg1"), lspecs.getSecurityGroups)

    //val specsWitUserData: LaunchSpecification = InstanceSpecs(instanceType = T1Micro, keyName = "keyName", securityGroups = List("sg1"), amiId = "amiId1", userData = "test test")
    //assertEquals("test test", specsWitUserData.getUserData)
  }

  @Test
  def base64Tests() {
    assertEquals("dGVzdHRlc3QK", Utils.base64encode("testtest\n"))
  }



}
