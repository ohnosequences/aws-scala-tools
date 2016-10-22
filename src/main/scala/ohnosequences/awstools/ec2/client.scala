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


case class ScalaEC2Client(val asJava: AmazonEC2) extends AnyVal { ec2 =>


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
      SpotInstanceRequest(ec2.asJava, request.getSpotInstanceRequestId)
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
      instance => new Instance(ec2.asJava, instance.getInstanceId)
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
        new Instance(ec2.asJava, instance.getInstanceId)
    }.toList
  }


  def listRequestsByFilters(filters: InstanceFilter*): List[SpotInstanceRequest] = {
    asJava.describeSpotInstanceRequests(
      new amzn.ec2.model.DescribeSpotInstanceRequestsRequest().withFilters(filters.map(_.toEC2Filter))
    ).getSpotInstanceRequests.map { request =>
      SpotInstanceRequest(ec2.asJava, request.getSpotInstanceRequestId)
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

  // FIXME: EC2MetadataClient is deprecated
  // def getCurrentInstanceId: Option[String] = {
  //   try {
  //     val m = new com.amazonaws.internal.EC2MetadataClient()
  //     Some(m.readResource("/latest/meta-data/instance-id"))
  //   } catch {
  //     case t: IOException => None
  //
  //   }
  // }
  //
  // def getCurrentInstance: Option[Instance] = getCurrentInstanceId.flatMap(getInstanceById(_))

  def getInstanceById(instanceId: String): Option[Instance] = {
    getEC2InstanceById(instanceId).map {
      ec2Instance =>
        new Instance(ec2.asJava, ec2Instance.getInstanceId)
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
