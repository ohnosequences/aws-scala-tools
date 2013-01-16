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
}


