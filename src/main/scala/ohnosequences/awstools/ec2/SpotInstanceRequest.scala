package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.CreateTagsRequest

import scala.collection.JavaConversions._

case class SpotInstanceRequest(val ec2: AmazonEC2, val request: com.amazonaws.services.ec2.model.SpotInstanceRequest) {

  def getSpotInstanceRequestId = request.getSpotInstanceRequestId

  def getTagValue(tagName: String) = request.getTags.find(_.getKey == tagName).map(_.getValue)

  def getInstanceId = request.getInstanceId

  def createTags(tags: awstools.ec2.Tag*) {
    ec2.createTags(new CreateTagsRequest().withResources(getSpotInstanceRequestId).withTags(tags.map(_.toECTag)))
  }
}
