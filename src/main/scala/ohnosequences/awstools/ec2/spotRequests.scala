package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model
import scala.collection.JavaConversions._


case class SpotInstanceRequest(
  val ec2: AmazonEC2,
  val requestId: String
) { spot =>

  def getSpotInstanceRequestId() = requestId


  private def getEC2Request(): model.SpotInstanceRequest = ec2.getEC2SpotRequestsById(requestId) match {
    case None => {
      throw new Error("Invalid instance of SpotInstanceRequest class")
    }
    case Some(requests) => requests
  }

  def getTagValue(tagName: String): Option[String] = {
    getEC2Request().getTags.find(_.getKey == tagName).map(_.getValue)
  }

  def getInstanceId(): Option[String] = {
    val id = getEC2Request().getInstanceId
    if(id.isEmpty) None else Some(id)
  }

  def createTags(tags: List[InstanceTag]): Unit = {
    ec2.createTags(requestId, tags)
  }

  def getState(): String = {
    getEC2Request().getState()
  }

  def getStatus(): String = {
    getEC2Request().getState
  }

}
