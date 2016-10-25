package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model
import scala.collection.JavaConversions._


case class SpotRequest(
  val ec2: AmazonEC2,
  val asJava: model.SpotInstanceRequest
) { spot =>

  lazy val id: String = asJava.getSpotInstanceRequestId

  // def getTagValue(tagName: String): Option[String] = {
  //   getEC2Request().getTags.find(_.getKey == tagName).map(_.getValue)
  // }
  //
  // def getInstanceId(): Option[String] = {
  //   val id = getEC2Request().getInstanceId
  //   if(id.isEmpty) None else Some(id)
  // }

  // def createTags(tags: List[InstanceTag]): Unit = {
  //   ec2.createTags(requestId, tags)
  // }

  // def getState(): String = {
  //   getEC2Request().getState()
  // }
  //
  // def getStatus(): String = {
  //   getEC2Request().getState
  // }

}
