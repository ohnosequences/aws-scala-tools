package ohnosequences.awstools.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.waiters.AmazonS3Waiters
import com.amazonaws.services.s3.model._
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.Try
import java.net.URL
import java.io.File
import java.time.Instant
import java.util.Date


case class ScalaS3Client(val asJava: AmazonS3) extends AnyVal { s3 =>

  def createTransferManager: TransferManager =
    TransferManagerBuilder.standard()
      .withS3Client(s3.asJava)
      .build()

  def withTransferManager[T](action: TransferManager => T): T = {
    val tm = createTransferManager
    val result = action(tm)
    tm.shutdownNow(false)
    result
  }

  def download(
    src: AnyS3Address,
    dst: File
  ): Try[File] = withTransferManager {
    _.download(src, dst)
  }

  def upload(
    src: File,
    dst: AnyS3Address
  ): Try[AnyS3Address] = withTransferManager {
    _.upload(src, dst)
  }

  def copy(
    src: S3Object,
    dst: S3Object
  ): Try[S3Object] = withTransferManager {
    _.copy(src, dst)
  }

  def copy(
    src: S3Folder,
    dst: S3Folder
  ): Try[List[S3Object]] = withTransferManager {
    _.copy(src, dst)
  }

  def waitUntil: AmazonS3Waiters = s3.asJava.waiters

  // TODO: rewrite using rotateTokens and listObjectsV2 method
  def listObjects(s3folder: S3Folder): Try[List[S3Object]] = {

    def listingToObjects(listing: ObjectListing): List[S3Object] =
      listing.getObjectSummaries.asScala.toList.map { summary =>
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
          s3.asJava.listNextBatchOfObjects(listing)
        )
      } else {
        (acc ++= listingToObjects(listing)).toList
      }
    }

    Try {
      keepListing(
        scala.collection.mutable.ListBuffer(),
        s3.asJava.listObjects(s3folder.bucket, s3folder.key)
      )
    }
  }


  def prefixExists(address: AnyS3Address): Boolean =
    Try {
      s3.asJava
        .listObjects(address.bucket, address.key)
        .getObjectSummaries.asScala
    }.filter{ _.nonEmpty }
      .isSuccess

  def objectExists(address: S3Object): Boolean =
    s3.asJava.doesObjectExist(address.bucket, address.key)

  def generateTemporaryLink(address: S3Object, linkLifeTime: FiniteDuration): Try[URL] = Try {
    val instant = Instant.now() plusMillis linkLifeTime.toMillis
    val date = new Date(instant.toEpochMilli())
    s3.asJava.generatePresignedUrl(address.bucket, address.key, date)
  }

}
