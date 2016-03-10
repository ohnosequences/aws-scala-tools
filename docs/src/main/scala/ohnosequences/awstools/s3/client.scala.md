
```scala
package ohnosequences.awstools.s3


import ohnosequences.logging.Logger

import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model.{ Region => _ , _ }
import com.amazonaws.services.s3.transfer.{Transfer, TransferManager}
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.event.{ProgressListener => PListener, ProgressEvent => PEvent}

import scala.collection.JavaConversions._
import scala.concurrent.duration._

import scala.util.{Failure, Success, Try}

import java.io.{IOException, InputStream, ByteArrayInputStream, File}
import java.net.URL


case class ScalaS3Client(val asJava: AmazonS3) extends AnyVal {

  def createTransferManager: TransferManager = new TransferManager(asJava)


  def uploadString(destination: S3Object, s: String): Try[Unit] = {
    Try {
      val array = s.getBytes
      val stream = new ByteArrayInputStream(array)
      val metadata = new ObjectMetadata()
      metadata.setContentLength(array.length)
      asJava.putObject(destination.bucket, destination.key, stream, metadata)
    }
  }


  def listObjects(s3folder: S3Folder): List[S3Object] = {

    def listingToObjects(listing: ObjectListing): List[S3Object] =
      listing.getObjectSummaries.toList.map { summary =>
        S3Object(summary.getBucketName, summary.getKey)
      }

    @scala.annotation.tailrec
    def keepListing(
      acc: scala.collection.mutable.ListBuffer[S3Object],
      listing: ObjectListing
    ): List[S3Object] = {

      if (listing.isTruncated) {
        keepListing(
          acc ++= listingToObjects(listing),
          asJava.listNextBatchOfObjects(listing)
        )
      } else {
        (acc ++= listingToObjects(listing)).toList
      }
    }

    keepListing(
      scala.collection.mutable.ListBuffer(),
      asJava.listObjects(s3folder.bucket, s3folder.key)
    )
  }


  def emptyBucket(name: String): Unit = {
    listObjects(S3Bucket(name)).foreach { objectAddress =>
      asJava.deleteObject(objectAddress.bucket, objectAddress.key)
    }
  }

  def deleteBucket(name: String, empty: Boolean = true): Unit = {
    if (asJava.doesBucketExist(name)) {
      if (empty) {
        emptyBucket(name)
      }
      asJava.deleteBucket(name)
    }
  }

  def objectExists(address: S3Object): Boolean = {
    Try(
      asJava.listObjects(address.bucket, address.key).getObjectSummaries
    ).filter{ _.length > 0 }.isSuccess
  }

  def generateTemporaryLink(address: S3Object, linkLifeTime: FiniteDuration): URL = {
    val deadline = linkLifeTime.fromNow
    val date = new java.util.Date(deadline.time.toMillis)
    asJava.generatePresignedUrl(address.bucket, address.key, date)
  }

}

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md