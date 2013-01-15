package ohnosequences.awstools.ec2

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.ec2.{AmazonEC2Client, AmazonEC2}


import scala.collection.JavaConversions._
import com.amazonaws.services.ec2.model._


object InstanceSpecs {
  implicit def getLaunchSpecs(specs: InstanceSpecs) = {
    val launchSpecs = new LaunchSpecification()
    launchSpecs.setSecurityGroups(specs.securityGroups)
    launchSpecs.setInstanceType(specs.instanceType)
    launchSpecs.setImageId(specs.amiId)
    launchSpecs.setKeyName(specs.keyName)
    launchSpecs.setUserData(Utils.base64encode(specs.userData))
    launchSpecs
  }
}

case class InstanceSpecs(
  instanceType   : InstanceType,
  amiId          : String,      
  securityGroups : List[String],
  keyName        : String,      
  userData       : String = "")

class EC2(val ec2: AmazonEC2) {

  def requestSpotInstances(amount: Int, price: String, specs: InstanceSpecs): 
    RequestSpotInstancesResult = {
    val spotRequest = new RequestSpotInstancesRequest()
    spotRequest.setSpotPrice(price)
    spotRequest.setInstanceCount(amount)
    spotRequest.setLaunchSpecification(specs)
    ec2.requestSpotInstances(spotRequest)
  }

  def runInstances(amount: Int, specs: InstanceSpecs): List[ohnosequences.awstools.ec2.Instance] = {
    val runRequest = new RunInstancesRequest(specs.amiId, amount, amount)
      .withInstanceType(specs.instanceType)
      .withKeyName(specs.keyName)
      .withUserData(Utils.base64encode(specs.userData))
      .withSecurityGroups(specs.securityGroups)
    ec2.runInstances(runRequest).getReservation().getInstances().map(ohnosequences.awstools.ec2.Instance(ec2, _)).toList
  }

  def getSpotPrice = {
    ec2.describeSpotPriceHistory(
      new DescribeSpotPriceHistoryRequest()
        .withStartTime(new java.util.Date())
        .withInstanceTypes(InstanceType.T1Micro.toString)
        .withProductDescriptions("Linux/UNIX")
    ).getSpotPriceHistory.map(_.getSpotPrice.toDouble).fold(1D)(math.min(_, _))
  }

  def createTag(instance: ohnosequences.awstools.ec2.Instance, tag: Tag) = {
    ec2.createTags(new CreateTagsRequest().withResources(instance.getInstanceId).withTags(tag))
  }



  def listInstancesByTag(tag: Tag) = {
    ec2.describeInstances(
      new DescribeInstancesRequest().withFilters(new Filter("tag:" + tag.getKey, List(tag.getValue)))
    ).getReservations().flatMap(_.getInstances)
  }


  def getInstancePublicDnsName(instanceId: String): Option[String] = {
    val instances = ec2.describeInstances().getReservations() flatMap (_.getInstances())
    instances find (_.getInstanceId() == instanceId) map (_.getPublicDnsName())
  }

  def terminateInstance(instanceId: String) {
    ec2.terminateInstances(new TerminateInstancesRequest(List(instanceId)))
  }

  def shutdown() {
    ec2.shutdown()
  }

  def getCurrentInstanceId = {
    io.Source.fromURL("http://169.254.169.254/latest/meta-data/instance-id").mkString
  }

  def getCurrentInstance: ohnosequences.awstools.ec2.Instance = getInstanceById(getCurrentInstanceId)

  def getInstanceById(instanceId: String): ohnosequences.awstools.ec2.Instance = {
    val instance = ec2.describeInstances(new DescribeInstancesRequest().withInstanceIds(instanceId)).getReservations.flatMap(_.getInstances).head
    Instance(ec2, instance)
  }

}

object EC2 {
  def create(credentialsFile: File): EC2 = {
    val ec2Client = new AmazonEC2Client(new PropertiesCredentials(credentialsFile))
    ec2Client.setEndpoint("http://ec2.eu-west-1.amazonaws.com")
    new EC2(ec2Client)
  }


}
