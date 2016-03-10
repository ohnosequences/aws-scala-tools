
```scala
package ohnosequences.awstools.s3

// import com.amazonaws.services.s3.model.{ Region => _ , _ }
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.model.{ S3Object => _, _ }
import com.amazonaws.event._
import com.amazonaws.event.{ProgressListener => PListener, ProgressEvent => PEvent}

import java.io.File

import scala.collection.JavaConversions._
import scala.concurrent._, Future._
import ExecutionContext.Implicits.global


case class TransferListener(transfer: Transfer) extends PListener {

  def progressChanged(progressEvent: PEvent): Unit = {
    import ProgressEventType._
    progressEvent.getEventType match {
      // case TRANSFER_STARTED_EVENT  => println("Started")
      case TRANSFER_CANCELED_EVENT  => println(s"${transfer.getDescription} is canceled")
      case TRANSFER_COMPLETED_EVENT => println(s"${transfer.getDescription} is completed")
      case TRANSFER_FAILED_EVENT    => println(s"${transfer.getDescription} is failed")
      // case TRANSFER_PART_COMPLETED_EVENT  => println("Completed part: "+ transfer.getProgress.getBytesTransferred)
      case _ => ()
    }
  }
}


// This is used for adding metadata to the S3 objects that we are uploading
case class s3MetadataProvider(metadataMap: Map[String, String]) extends ObjectMetadataProvider {

  def provideObjectMetadata(file: java.io.File, metadata: ObjectMetadata): Unit = {
    metadata.setUserMetadata(metadataMap)
  }
}


case class TransferManagerOps(asJava: TransferManager) {

  // by default shutdownNow shuts down the S3 client as well
  def shutdown(shutDownS3Client: Boolean = false): Unit =
    asJava.shutdownNow(shutDownS3Client)


  def download(
    s3Address: AnyS3Address,
    destination: File
  ): Future[File] = {
    println(s"""Dowloading object
      |from: ${s3Address}
      |to: ${destination.getCanonicalPath}
      |""".stripMargin
    )

    lazy val transfer: Transfer = s3Address match {
      case S3Object(bucket, key) => asJava.download(bucket, key, destination)
      case S3Folder(bucket, key) => asJava.downloadDirectory(bucket, key, destination)
    }

    Future {
      transfer.addProgressListener(TransferListener(transfer))

      // NOTE: this is blocking:
      transfer.waitForCompletion

      // if this was a virtual directory, the destination actually differs:
      s3Address match {
        case S3Object(_, key) => destination
        case S3Folder(_, key) => new File(destination, key)
      }
    }
  }

  def upload(
    file: File,
    s3Address: AnyS3Address,
    userMetadata: Map[String, String] = Map()
  ): Future[AnyS3Address] = {
    println(s"""Uploading object
      |from: ${file.getCanonicalPath}
      |to: ${s3Address}
      |""".stripMargin
    )

    lazy val transfer: Transfer = if (file.isDirectory) {
      asJava.uploadDirectory(
        s3Address.bucket,
        s3Address.key,
        file,
        true, // includeSubdirectories
        s3MetadataProvider(userMetadata)
      )
    } else {
      val request = new PutObjectRequest(
        s3Address.bucket,
        s3Address.key,
        file
      )

      val metadata = new ObjectMetadata()
      metadata.setUserMetadata(userMetadata)

      asJava.upload( request.withMetadata(metadata) )
    }

    Future {
      transfer.addProgressListener(TransferListener(transfer))

      // NOTE: this is blocking:
      transfer.waitForCompletion
      s3Address
    }
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