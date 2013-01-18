package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.{CancelSpotInstanceRequestsRequest, Tag}

import scala.collection.JavaConversions._

case class SpotInstanceRequest(val ec2: AmazonEC2, val request: com.amazonaws.services.ec2.model.SpotInstanceRequest) {

  def getSpotInstanceRequestId = request.getSpotInstanceRequestId

  def getTagValue(tagName: String) = request.getTags.find(_.getKey == tagName).map(_.getValue)

  def getInstanceId = request.getInstanceId

//  def cancel = ec2.cancelSpotInstanceRequests(
//    new CancelSpotInstanceRequestsRequest(getSpotInstanceRequestId)
//  )

//  def createTag(tag: Tag) {
//    ec2.createTag(getSpotInstanceRequestId)
//  }
}
