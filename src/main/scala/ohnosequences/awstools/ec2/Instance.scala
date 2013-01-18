package ohnosequences.awstools.ec2

import com.amazonaws.services._
import ec2.AmazonEC2
import ec2.model.{CreateTagsRequest, Tag, TerminateInstancesRequest}

import scala.collection.JavaConversions._

case class Instance(val ec2: AmazonEC2, instance: com.amazonaws.services.ec2.model.Instance) {

  def terminate = ec2.terminateInstances(new TerminateInstancesRequest(List(instance.getInstanceId)))

  def createTag(tag: Tag) {
    ec2.createTags(new CreateTagsRequest(List(instance.getInstanceId), List(tag)))
  }

  def getTagValue(tagName: String) = {
    instance.getTags.find(_.getKey == tagName).map(_.getValue)
  }

  def getInstanceId = instance.getInstanceId

  def getSSHCommand = {
    //ssh -i evdokim.pem ec2-user@ec2-46-137-141-37.eu-west-1.compute.amazonaws.com
    val keyPairFile = instance.getKeyName + ".pem"
    val publicDNS = instance.getPublicDnsName
    "ssh -i " + keyPairFile + " ec2-user@" + publicDNS
  }
}


