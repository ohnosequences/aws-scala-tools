
```scala
package ohnosequences.awstools.s3

import com.amazonaws.{ AmazonClientException, AmazonServiceException }
import com.amazonaws.services.s3.model.{ S3Object => _, _ }
import com.amazonaws.services.s3.transfer._
import com.amazonaws.event.{ ProgressListener => PListener, ProgressEvent => PEvent, _ }

import java.io.File

import scala.collection.JavaConversions._
import scala.util.Try


case class TransferListener(description: String) extends PListener {

  def progressChanged(progressEvent: PEvent): Unit = {
    import ProgressEventType._
    progressEvent.getEventType match {
      case TRANSFER_STARTED_EVENT   => print(description)
      case TRANSFER_PART_COMPLETED_EVENT => print(".")
      case TRANSFER_PART_FAILED_EVENT    => print("!")
      case TRANSFER_CANCELED_EVENT  => println(" canceled")
      case TRANSFER_COMPLETED_EVENT => println(" completed")
      case TRANSFER_FAILED_EVENT    => println(" failed")
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
    destination: File,
    silent: Boolean = true
  ): Try[File] = {

    lazy val transfer: Transfer = s3Address match {
      case S3Object(bucket, key) => asJava.download(bucket, key, destination)
      case S3Folder(bucket, key) => asJava.downloadDirectory(bucket, key, destination)
    }

    Try {
      if (!silent) {
        transfer.addProgressListener(TransferListener(s"${transfer.getDescription} to ${destination.getCanonicalPath}"))
      }
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
    userMetadata: Map[String, String] = Map(),
    silent: Boolean = true
  ): Try[AnyS3Address] = {

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

    Try {
      if (!silent) {
        transfer.addProgressListener(TransferListener(s"${transfer.getDescription} to ${file.getCanonicalPath}"))
      }
      transfer.waitForCompletion
      s3Address
    }
  }

}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: transfers.scala.md
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