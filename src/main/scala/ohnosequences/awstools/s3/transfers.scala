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
      case TRANSFER_STARTED_EVENT  => println("Started")
      case TRANSFER_CANCELED_EVENT  => println("Canceled!")
      case TRANSFER_COMPLETED_EVENT  => println("Completed!")
      case TRANSFER_FAILED_EVENT  => println("Failed!")
      case TRANSFER_PART_COMPLETED_EVENT  => println("Completed part: "+ transfer.getProgress.getBytesTransferred)
      case TRANSFER_PART_FAILED_EVENT  => println("Failed part transfer")
      case TRANSFER_PART_STARTED_EVENT  => println("Started part transfer")
      case TRANSFER_PREPARING_EVENT  => println("Preparing for the transfer")
      // case HTTP_REQUEST_CONTENT_RESET_EVENT  => ()
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

  val transferWaiter: (Transfer => Unit) = { transfer =>
    while(!transfer.isDone) {
      println(" - Progress: " + transfer.getProgress.getBytesTransferred + " bytes")
      Thread.sleep(500)
    }
    println("Finished: " + transfer.getState)
  }

  // Asinchronous progress listener
  val transferListener: (Transfer => Unit) = { transfer =>
    transfer.addProgressListener(TransferListener(transfer))
  }

  def download(
    s3Address: AnyS3Address,
    destination: File
  ): Future[File] = {
    println(s"""Dowloading object
      |from: ${s3Address}
      |to: ${destination.getCanonicalPath}
      |""".stripMargin
    )

    val transfer: Transfer = s3Address match {
      case S3Object(bucket, key) => asJava.download(bucket, key, destination)
      case S3Folder(bucket, key) => asJava.downloadDirectory(bucket, key, destination)
    }

    // This should attach a default progress listener
    transfer.addProgressListener(new ProgressTracker())

    Future {
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

    val transfer: Transfer = if (file.isDirectory) {
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

    // This should attach a default progress listener
    transfer.addProgressListener(new ProgressTracker())

    Future {
      // NOTE: this is blocking:
      transfer.waitForCompletion
      s3Address
    }
  }

}
