package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model
import scala.collection.JavaConversions._


case class InstanceStatus(val instanceStatus: String, val systemStatus: String)


case class Instance(
  val ec2: AmazonEC2,
  // val asJava: model.Instance
  val instanceId: String
) { instance =>

  private def getEC2Instance(): model.Instance = ec2.getEC2InstanceById(instanceId) match {
    case None => {
      throw new Error("Invalid instance of Instance class")
    }
    case Some(instance) => instance
  }

  def terminate(): Unit = {
    ec2.terminateInstance(instanceId)
  }

  def createTag(tag: InstanceTag): Unit = {
    ec2.createTags(instanceId, List(tag))
  }

  def createTags(tags: List[InstanceTag]): Unit = {
    ec2.createTags(instanceId, tags)
  }

  def getTagValue(tagName: String): Option[String] = {
    getEC2Instance().getTags.find(_.getKey == tagName).map(_.getValue)
  }

  def getInstanceId() = instanceId

  def getSSHCommand(): Option[String] = {
    val instance = getEC2Instance()
    val keyPairFile = instance.getKeyName + ".pem"
    val publicDNS = instance.getPublicDnsName
    if (!publicDNS.isEmpty) {
      Some("ssh -i " + keyPairFile + " ec2-user@" + publicDNS)
    } else {
      None
    }
  }

  def getAMI(): String = {
    val instance = getEC2Instance()
    instance.getImageId()
  }

  // FIXME: kinda deprecated
  // def getInstanceType(): AnyInstanceType = {
  //   val instance = getEC2Instance()
  //   InstanceType.fromName(instance.getInstanceType)
  // }


  def getState(): String = {
    getEC2Instance().getState().getName
  }

  def getStatus(): Option[InstanceStatus] = {
    val statuses = ec2.describeInstanceStatus(new model.DescribeInstanceStatusRequest()
      .withInstanceIds(instanceId)
      ).getInstanceStatuses()
    if (statuses.isEmpty) None
    else {
      val is = statuses.head
      Some(InstanceStatus(
          is.getInstanceStatus().getStatus()
        , is.getSystemStatus().getStatus()
        )
      )
    }
  }

  def getPublicDNS(): Option[String] = {
    val dns = getEC2Instance().getPublicDnsName()
    if (dns.isEmpty) None else Some(dns)
  }
}
