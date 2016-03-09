package ohnosequences.awstools.s3

import ohnosequences.awstools.regions._
import ohnosequences.logging.Logger

import com.amazonaws.services.s3.model.{ Region => _ , _ }
import com.amazonaws.services.s3.transfer.{Transfer, TransferManager}
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.event._
import com.amazonaws.event.{ProgressListener => PListener, ProgressEvent => PEvent}

import java.io.File


case class TransferListener(transfer: Transfer) extends PListener {
  def progressChanged(progressEvent: PEvent) {
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

case class LoadingManager(transferManager: TransferManager, logger: Option[Logger] = None) {

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

  def upload(
    objectAddress: S3Object,
    file: File,
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Uploading to: " + objectAddress.toString)
    println("File: " + file.getAbsolutePath)
    val upload = transferManager.upload(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(upload)
  }

  def uploadDirectory(
    objectAddress: S3Object,
    directory: File,
    recursively: Boolean = true,
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Uploading to: " + objectAddress.toString)
    println("Directory: " + directory.getCanonicalPath + (if (recursively) " (recursively)" else ""))
    val upload = transferManager.uploadDirectory(objectAddress.bucket, objectAddress.key, directory, recursively)
    transferWaiter(upload)
  }

  def download(
    objectAddress: S3Object,
    file: File,
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Dowloading from: " + objectAddress.toString)
    println("File: " + file.getAbsolutePath)
    val download = transferManager.download(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(download)
  }

  def downloadDirectory(
    objectAddress: S3Object,
    destinationDir: File,
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Dowloading from: " + objectAddress.toString)
    println("To directory: " + destinationDir.getAbsolutePath)
    val download = transferManager.downloadDirectory(objectAddress.bucket, objectAddress.key, destinationDir)
    transferWaiter(download)
  }

  def shutdown() {
    transferManager.shutdownNow(false)
  }
}
