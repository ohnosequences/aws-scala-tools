package ohnosequences.awstools.s3

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.transfer._
import com.amazonaws.services.s3.waiters.AmazonS3Waiters
import com.amazonaws.services.s3.model._
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.util.Try
import java.net.URL


case class ScalaS3Client(val asJava: AmazonS3) extends AnyVal { s3 =>

  def createTransferManager: TransferManager =
    TransferManagerBuilder.standard()
      .withS3Client(s3.asJava)
      .build()

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
    val date = new java.util.Date(linkLifeTime.toMillis)
    s3.asJava.generatePresignedUrl(address.bucket, address.key, date)
  }

}
