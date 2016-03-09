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

  def createLoadingManager(): LoadingManager = new LoadingManager(new TransferManager(asJava))

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
