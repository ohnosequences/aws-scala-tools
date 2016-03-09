package ohnosequences.awstools.s3


import ohnosequences.awstools.regions._
import ohnosequences.logging.Logger

import com.amazonaws.auth._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model.{ Region => _ , _ }
import com.amazonaws.services.s3.transfer.{Transfer, TransferManager}
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.event._
import com.amazonaws.event.{ProgressListener => PListener, ProgressEvent => PEvent}

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration

import scala.util.{Failure, Success, Try}

import java.io.{IOException, InputStream, ByteArrayInputStream, File}
import java.net.URL


class S3(val s3: AmazonS3) {

  def createLoadingManager(): LoadingManager = new LoadingManager(new TransferManager(s3))

  def copy(src: S3Object, dst: S3Object): Boolean = {
    try {
      s3.copyObject(src.bucket, src.key, dst.bucket, dst.key)
      true
    } catch {
      case t: Throwable => t.printStackTrace(); false
    }
  }

  def getObjectString(objectAddress: S3Object): Try[String] = {
    Try {
      s3.getObject(objectAddress.bucket, objectAddress.key)
    }.flatMap { s3obj =>
      Try {
        val s = scala.io.Source.fromInputStream(s3obj.getObjectContent).mkString
        s3obj.close()
        s
      }.recoverWith { case t =>
        s3obj.close()
        Failure(t)
      }
    }.recoverWith { case t =>
      Failure(new Error("failed to retrive content of " + objectAddress, t))
    }
  }

  def getObjectStream(objectAddress: S3Object): InputStream = {
    s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
  }



  def uploadFile(destination: S3Object, file: File, public: Boolean = false): Try[Unit] = {
    Try {
      if (public) {
        s3.putObject(new PutObjectRequest(destination.bucket, destination.key, file).withCannedAcl(CannedAccessControlList.PublicRead))
      } else {
        s3.putObject(new PutObjectRequest(destination.bucket, destination.key, file))
      }
    }
  }

  def uploadString(destination: S3Object, s: String): Try[Unit] = {
    Try {
      val array = s.getBytes
      val stream = new ByteArrayInputStream(array)
      val metadata = new ObjectMetadata()
      metadata.setContentLength(array.length)
      s3.putObject(destination.bucket, destination.key, stream, metadata)
    }
  }


  def deleteObject(objectAddress: S3Object) {
    s3.deleteObject(objectAddress.bucket, objectAddress.key)
  }

  def deleteBucket(name: String, empty: Boolean = true) {
    if (s3.doesBucketExist(name)) {
      if (empty) {
        emptyBucket(name)
      }
      s3.deleteBucket(name)
    }
  }


  def listObjects(bucket: String, prefix: String = ""): List[S3Object] = {
   // println("lsitObject")
    val result = ListBuffer[S3Object]()
    //var stopped = false
    var listing = s3.listObjects(bucket, prefix)

    result ++= listing.getObjectSummaries.map{ summary =>
        S3Object(bucket, summary.getKey)
    }

     while (listing.isTruncated) {
      //listing = Some(s3.listObjects(bucket, prefix))
     // println(".")

      listing = s3.listNextBatchOfObjects(listing)
      result ++= listing.getObjectSummaries.map{ summary =>
        S3Object(bucket, summary.getKey)
      }

    }
    result.toList
  }


  def downloadDirectory(bucket: String, prefix: String) {
    var stopped = false
    while(!stopped) {
      val listing = s3.listObjects(bucket, prefix)
      println(listing.getObjectSummaries.map(_.getKey))
      if(!listing.isTruncated) {
        stopped = true
      }
    }
  }


  def emptyBucket(name: String) {
    listObjects(name).foreach{ objectAddress =>
      s3.deleteObject(name, objectAddress.key)
    }
    s3.listObjects(name).getObjectSummaries.foreach { objectSummary =>
      s3.deleteObject(objectSummary.getBucketName, objectSummary.getKey)
    }
  }

  def objectExists(address: S3Object): Try[Boolean] = {
    Try {
      val metadata = s3.getObjectMetadata(address.bucket, address.key)
      metadata != null
    }.recoverWith { case t =>
      Failure(new Error("unable to access " + address))
    }
  }

  def generateTemporaryLink(address: S3Object, linkLifeTime: Duration): Try[URL] = {
    Try {
      val exp = new java.util.Date()
      var expMs = exp.getTime()
      expMs += linkLifeTime.toMillis
      exp.setTime(expMs)
      s3.generatePresignedUrl(address.bucket, address.key, exp)
    }
  }

}

object S3 {

  def create(): S3 = {
    create(new InstanceProfileCredentialsProvider())
  }

  def create(credentialsFile: File): S3 = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)))
  }

  def create(accessKey: String, secretKey: String): S3 = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
  }

  def create(credentials: AWSCredentialsProvider, region: Region = Region.Ireland): S3 = {
    val s3Client = new AmazonS3Client(credentials)
    s3Client.setRegion(region.toAWSRegion)
    new S3(s3Client)
  }
}
