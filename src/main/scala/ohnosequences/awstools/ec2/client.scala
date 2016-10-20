package ohnosequences.awstools.ec2

import java.io.{IOException, PrintWriter, File}

import ohnosequences.awstools.regions._

import com.amazonaws.auth._
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.services.ec2.{ AmazonEC2, AmazonEC2Client }
import com.amazonaws.{ services => amzn }

import scala.util.Try
import scala.collection.JavaConversions._
import com.amazonaws.AmazonServiceException


case class InstanceStatus(val instanceStatus: String, val systemStatus: String)

case class ScalaEC2Client(val asJava: AmazonEC2)
// extends AnyVal
{ ec2 =>

  class Instance(instanceId: String) {

    private def getEC2Instance(): amzn.ec2.model.Instance = ec2.getEC2InstanceById(instanceId) match {
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
      val statuses = asJava.describeInstanceStatus(new amzn.ec2.model.DescribeInstanceStatusRequest()
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

  class SpotInstanceRequest(requestId: String) {

    def getSpotInstanceRequestId() = requestId


    private def getEC2Request(): amzn.ec2.model.SpotInstanceRequest = ec2.getEC2SpotRequestsById(requestId) match {
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


  def isKeyPairExists(name: String): Boolean = {
    try {
      val pairs = asJava.describeKeyPairs(new amzn.ec2.model.DescribeKeyPairsRequest()
        .withKeyNames(name)
      ).getKeyPairs
      // println("here keypaurs " + pairs)
      !pairs.isEmpty
    } catch {
      case e: Throwable => false
    }
  }

  def createKeyPair(name: String, file: Option[File]): Unit = {
    if (!isKeyPairExists(name)) {
      val keyPair = asJava.createKeyPair(new amzn.ec2.model.CreateKeyPairRequest()
        .withKeyName(name)
      ).getKeyPair

      file.foreach { file =>
        val keyContent = keyPair.getKeyMaterial
        val writer = new PrintWriter(file)
        writer.print(keyContent)
        writer.close()

        //chmod 400
        file.setWritable(false, false)
        file.setReadable(false, false)
        file.setExecutable(false, false)
        file.setReadable(true, true)
      }
    }
  }

  def deleteKeyPair(name: String): Unit = {
    asJava.deleteKeyPair(new amzn.ec2.model.DeleteKeyPairRequest()
      .withKeyName(name)
    )
  }

  def deleteSecurityGroup(name: String, attempts: Int = 0): Boolean = {
    try {
      asJava.deleteSecurityGroup(new amzn.ec2.model.DeleteSecurityGroupRequest()
        .withGroupName(name)
      )
      true
    } catch {
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidGroup.InUse") => {
        if(attempts > 0) {
          Thread.sleep(2000)
          println("security group: " + name + " in use, waiting...")
          deleteSecurityGroup(name, attempts-1)
        } else {
          false
        }
      }
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidGroup.NotFound") => true
    }
  }

  def enableSSHPortForGroup(name: String): Unit = {
    enablePortForGroup(name, 22)
  }

  def enablePortForGroup(name: String, port: Int): Unit = {
    try {
      asJava.authorizeSecurityGroupIngress(new amzn.ec2.model.AuthorizeSecurityGroupIngressRequest()
        .withGroupName(name)
        .withIpPermissions(new amzn.ec2.model.IpPermission()
        .withFromPort(port)
        .withToPort(port)
        .withIpRanges("0.0.0.0/0")
        .withIpProtocol("tcp")
      )
      )
    } catch {
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidPermission.Duplicate") => ()
    }

  }

  def createSecurityGroup(name: String): Unit = {
    try {
      asJava.createSecurityGroup(new amzn.ec2.model.CreateSecurityGroupRequest()
        .withGroupName(name)
        .withDescription(name)
      )
    } catch {
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidGroup.Duplicate") => ()
    }
  }

  def requestSpotInstances(amount: Int, price: Double, specs: AnyLaunchSpecs, timeout: Int = 36000): List[SpotInstanceRequest] = {
    asJava.requestSpotInstances(new amzn.ec2.model.RequestSpotInstancesRequest()
      .withSpotPrice(price.toString)
      .withInstanceCount(amount)
      .withLaunchSpecification(specs.toAWS)
    ).getSpotInstanceRequests.map{ request =>
      new SpotInstanceRequest(request.getSpotInstanceRequestId)
    }.toList
  }

  def runInstances(amount: Int, specs: AnyLaunchSpecs): List[Instance] = {
    val preRequest = new amzn.ec2.model.RunInstancesRequest(specs.instanceSpecs.ami.id, amount, amount)
      .withInstanceType(specs.instanceSpecs.instanceType.toAWS)
      .withKeyName(specs.keyName)
      .withUserData(base64encode(specs.userData))
      .withSecurityGroups(specs.securityGroups)

     // add IAM instance profile if needed
    val request = specs.instanceProfile match {
      case None => preRequest
      case Some(name) => preRequest.withIamInstanceProfile(
        new amzn.ec2.model.IamInstanceProfileSpecification().withName(name)
      )
    }

    asJava.runInstances(request).getReservation.getInstances.toList.map {
      instance => new Instance(instance.getInstanceId)
    }
  }

  def getCurrentSpotPrice(instanceType: AnyInstanceType, productDescription: String = "Linux/UNIX"): Double = {
    asJava.describeSpotPriceHistory(
      new amzn.ec2.model.DescribeSpotPriceHistoryRequest()
        .withStartTime(new java.util.Date())
        .withInstanceTypes(instanceType.name)
        .withProductDescriptions(productDescription)
    ).getSpotPriceHistory
     .map{ _.getSpotPrice.toDouble }
     .fold(0D){ math.max(_, _) }
  }


  def createTags(resourceId: String, tags: List[InstanceTag]): Unit = {
    asJava.createTags(new amzn.ec2.model.CreateTagsRequest()
      .withResources(resourceId)
      .withTags(tags.map(_.toECTag))
    )
  }

  def listInstancesByFilters(filters: InstanceFilter*): List[Instance] = {
    asJava.describeInstances(
      new amzn.ec2.model.DescribeInstancesRequest().withFilters(filters.map(_.toEC2Filter))
    ).getReservations.flatMap(_.getInstances).map { instance =>
        new Instance(instance.getInstanceId)
    }.toList
  }


  def listRequestsByFilters(filters: InstanceFilter*): List[SpotInstanceRequest] = {
    asJava.describeSpotInstanceRequests(
      new amzn.ec2.model.DescribeSpotInstanceRequestsRequest().withFilters(filters.map(_.toEC2Filter))
    ).getSpotInstanceRequests.map { request =>
      new SpotInstanceRequest(request.getSpotInstanceRequestId)
    }.toList
  }


  def terminateInstance(instanceId: String): Unit = {
    try {
      asJava.terminateInstances(new amzn.ec2.model.TerminateInstancesRequest(List(instanceId)))
    } catch {
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidInstanceID.NotFound") => ()
    }
  }

  def cancelSpotRequest(requestId: String): Unit = {
    asJava.cancelSpotInstanceRequests(new amzn.ec2.model.CancelSpotInstanceRequestsRequest(List(requestId)))
  }

  def shutdown(): Unit = {
    asJava.shutdown()
  }

  def getCurrentInstanceId: Option[String] = {
    try {
      val m = new com.amazonaws.internal.EC2MetadataClient()
      Some(m.readResource("/latest/meta-data/instance-id"))
    } catch {
      case t: IOException => None

    }
  }

  def getCurrentInstance: Option[Instance] = getCurrentInstanceId.flatMap(getInstanceById(_))

  def getInstanceById(instanceId: String): Option[Instance] = {
    getEC2InstanceById(instanceId).map {
      ec2Instance =>
        new Instance(ec2Instance.getInstanceId)
    }
  }

  def getEC2InstanceById(instanceId: String): Option[amzn.ec2.model.Instance] = {
    try {
    asJava.describeInstances(new amzn.ec2.model.DescribeInstancesRequest()
      .withInstanceIds(instanceId)
    ).getReservations.flatMap(_.getInstances).headOption
    } catch {
      case e: AmazonServiceException if e.getErrorCode().equals("InvalidInstanceID.NotFound") => None
    }
  }

  def getEC2SpotRequestsById(requestsId: String): Option[amzn.ec2.model.SpotInstanceRequest] = {
    asJava.describeSpotInstanceRequests(new amzn.ec2.model.DescribeSpotInstanceRequestsRequest()
      .withSpotInstanceRequestIds(requestsId)
    ).getSpotInstanceRequests.headOption
  }

  def getAllAvailableZones: Set[String] = {
    ec2.asJava.describeAvailabilityZones(
      new amzn.ec2.model.DescribeAvailabilityZonesRequest()
        .withFilters(
          new amzn.ec2.model.Filter("state", List("available"))
        )
    ).getAvailabilityZones
      .map{ _.getZoneName }.toSet
  }

}