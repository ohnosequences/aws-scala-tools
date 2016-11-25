
```scala
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
```

Example usage:

    ```scala
    ec2.runInstances(...)(10).map { instances =>
 ec2.waitUntil.instanceStatusOk.withIDs(instances.map(_.id))
    }
    ```

    This will launch instances and wait (blocking) until their statuses are OK. You can use it similarly for waiting spot-requests fulfillment.


```scala
  def waitUntil: AmazonEC2Waiters = asJava.waiters
```

Launches on-demand instances

```scala
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
```

Requests spot-instances with the given price

```scala
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

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md