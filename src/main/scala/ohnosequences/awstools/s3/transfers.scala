package ohnosequences.awstools.s3

import com.amazonaws.services.s3.model.{ S3Object => _, _ }
import com.amazonaws.services.s3.transfer._
import com.amazonaws.event.{ ProgressListener => PListener, ProgressEvent => PEvent, _ }

import java.io.File

import scala.collection.JavaConverters._
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
    metadata.setUserMetadata(metadataMap.asJava)
  }
}


case class ScalaTransferManager(asJava: TransferManager) { tm =>

  // by default shutdownNow shuts down the S3 client as well
  def shutdown(shutDownS3Client: Boolean = false): Unit =
    tm.asJava.shutdownNow(shutDownS3Client)


  def download(
    s3Address: AnyS3Address,
    destination: File,
    silent: Boolean = true
  ): Try[File] = {

    lazy val transfer: Transfer = s3Address match {
      case S3Object(bucket, key) => tm.asJava.download(bucket, key, destination)
      case S3Folder(bucket, key) => tm.asJava.downloadDirectory(bucket, key, destination)
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
      tm.asJava.uploadDirectory(
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
      metadata.setUserMetadata(userMetadata.asJava)

      tm.asJava.upload( request.withMetadata(metadata) )
    }

    Try {
      if (!silent) {
        transfer.addProgressListener(TransferListener(s"${transfer.getDescription} to ${file.getCanonicalPath}"))
      }
      transfer.waitForCompletion
      s3Address
    }
  }

  def copy(
    source:      S3Object,
    destination: S3Object
  ): Try[S3Object] = {
    Try {
      tm.asJava.copy(
        source.bucket, source.key,
        destination.bucket, destination.key
      ).waitForCompletion

      destination
    }
  }

  def copy(
    source:      S3Folder,
    destination: S3Folder
  ): Try[List[S3Object]] = {

    val s3 = tm.asJava.getAmazonS3Client

    val listingResult: Try[List[(S3Object, S3Object)]] =
      s3.listObjects(source).map { list =>
        list.map { obj =>
          val suffix = source.toURI.relativize(obj.toURI)
          val newURI = destination.toURI.resolve(suffix)
          obj -> S3Object(newURI)
        }
      }

    // This should start all transfers
    listingResult.map { list =>
      val transfers = list.map { case (src, dst) =>
        tm.asJava.copy(
          src.bucket, src.key,
          dst.bucket, dst.key
        )
      }

      // Now we wait for all of them to finish
      transfers.foreach { _.waitForCompletion }

      list.map(_._2)
    }
  }
}
