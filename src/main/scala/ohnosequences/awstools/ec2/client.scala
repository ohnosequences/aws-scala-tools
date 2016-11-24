package ohnosequences.awstools.ec2

import ohnosequences.awstools._, regions._
import com.amazonaws.auth._
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.services.ec2.{ AmazonEC2, AmazonEC2Client }
import com.amazonaws.services.ec2.model._
import com.amazonaws.services.ec2.waiters._
import scala.util.Try
import scala.collection.JavaConversions._
import com.amazonaws.AmazonServiceException


case class ScalaEC2Client(val asJava: AmazonEC2) extends AnyVal { ec2 =>

  /* Example usage:

    ```scala
    ec2.runInstances(...)(10).map { instances =>
      ec2.waitUntil.instanceStatusOk.withIDs(instances.map(_.id))
    }
    ```

    This will launch instances and wait (blocking) until their statuses are OK. You can use it similarly for waiting spot-requests fulfillment.
  */
  def waitUntil: AmazonEC2Waiters = asJava.waiters

  /* Launches on-demand instances */
  def runInstances(launchSpecs: AnyLaunchSpecs)(
    amount: Int = 1
  ): Try[Seq[Instance]] = Try {

    ec2.asJava.runInstances(
      launchSpecs.toRunInstancesRequest
        .withMinCount(amount)
        .withMaxCount(amount)
        // TODO: .withPlacement
    ).getReservation
      .getInstances
      .map { Instance(ec2.asJava, _) }
  }

  // TODO: instance-specific filter type
  def filterInstances(filters: Filter*): Try[Stream[Instance]] = Try {

    val request = new DescribeInstancesRequest().withFilters(filters)

    def fromResponse(response: DescribeInstancesResult) = (
      Option(response.getNextToken),
      response
        .getReservations
        .flatMap(_.getInstances)
        .map { Instance(ec2.asJava, _) }
    )

    rotateTokens { token =>
      fromResponse(ec2.asJava.describeInstances(
        token.fold(request)(request.withNextToken)
      ))
    }
  }

  def getInstance(instanceID: String): Try[Instance] = Try {
    ec2.asJava.describeInstances(
      new DescribeInstancesRequest().withInstanceIds(instanceID)
    ).getReservations
      .flatMap { _.getInstances }
      .map { Instance(ec2.asJava, _) }
      .headOption.getOrElse {
        throw new java.util.NoSuchElementException(s"Instance [${instanceID}] doesn't exist")
      }
  }

  def getCurrentInstance: Try[Instance] =
    getLocalMetadata("instance-id").flatMap(getInstance)


  def currentSpotPrice(instanceType: AnyInstanceType): Try[Double] = Try {
    ec2.asJava.describeSpotPriceHistory(
      new DescribeSpotPriceHistoryRequest()
        .withStartTime(new java.util.Date())
        .withProductDescriptions("Linux/UNIX")
        .withInstanceTypes(instanceType)
    ).getSpotPriceHistory
      .map{ _.getSpotPrice.toDouble }
      .fold(0D){ math.max(_, _) }
  }

  /* Requests spot-instances with the given price */
  def requestSpotInstances(launchSpecs: AnyLaunchSpecs)(
    amount: Int = 1,
    price: Double
  ): Try[Seq[SpotInstanceRequest]] = Try {

    asJava.requestSpotInstances(
      new RequestSpotInstancesRequest()
        .withLaunchSpecification(launchSpecs.toLaunchSpecification)
        .withInstanceCount(amount)
        .withSpotPrice(price.toString)
    ).getSpotInstanceRequests
  }


  def getAllAvailableZones: Set[String] = {
    ec2.asJava.describeAvailabilityZones(
      new DescribeAvailabilityZonesRequest()
        .withFilters(
          new Filter("state", List("available"))
        )
    ).getAvailabilityZones
      .map{ _.getZoneName }.toSet
  }

  def keyPairExists(name: String): Try[Boolean] = Try {
    asJava.describeKeyPairs(
      new DescribeKeyPairsRequest().withKeyNames(name)
    ).getKeyPairs.nonEmpty
  }

  def createKeyPair(name: String): Try[KeyPair] = Try {
    asJava.createKeyPair(
      new CreateKeyPairRequest().withKeyName(name)
    ).getKeyPair
  }

}
