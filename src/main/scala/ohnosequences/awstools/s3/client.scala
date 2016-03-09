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

  @scala.annotation.tailrec
  final def tryAction[T](action: => Option[T], attemptsLeft: Int = 10, timeOut: Int = 500): Option[T] = {
    if(attemptsLeft <= 0) None
    else {
      action match {
        case Some(t) => Some(t)
        case None => {
          Thread.sleep(timeOut)
          tryAction(action, attemptsLeft - 1)
        }
      }
    }
  }

  def createBucket(name: String): Option[Boolean] = {
    def createBucketAction: Option[Boolean] = {
      try {
        s3.createBucket(name)
        Some(true)
      } catch {
        case e: AmazonServiceException if e.getStatusCode == 409 => Some(true)
        case e: AmazonServiceException => println("warning: " + e.toString); None
      }
    }
    tryAction(createBucketAction)
  }

  def bucketExists(name: String) = s3.doesBucketExist(name)

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

  @deprecated("", since = "v0.13.1")
  def readWholeObject(objectAddress: S3Object) = {
    val objectStream = s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
    scala.io.Source.fromInputStream(objectStream).mkString
  }

  @deprecated("", since = "v0.13.1")
  def readObject(objectAddress: S3Object): Option[String] = {
    try {
      val objectStream = s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
      Some(scala.io.Source.fromInputStream(objectStream).mkString)
    } catch {
      case t: Throwable => None
    }
  }

  def getObjectStream(objectAddress: S3Object): InputStream = {
    s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
  }



  @deprecated("use uploadString()", since = "v0.13.1")
  def putWholeObject(objectAddress: S3Object, content: String): Unit = {
    val array = content.getBytes

    val stream = new ByteArrayInputStream(array)
    val metadata = new ObjectMetadata()
    metadata.setContentLength(array.length)
    s3.putObject(objectAddress.bucket, objectAddress.key, stream, metadata)
  }

  @deprecated("use uploadFile()", since = "v0.13.1")
  def putObject(objectAddress: S3Object, file: File, public: Boolean = false) {
    createBucket(objectAddress.bucket)
    if (public) {
      s3.putObject(new PutObjectRequest(objectAddress.bucket, objectAddress.key, file).withCannedAcl(CannedAccessControlList.PublicRead))
    } else {
      s3.putObject(new PutObjectRequest(objectAddress.bucket, objectAddress.key, file))
    }
  }

  def uploadFile(destination: S3Object, file: File, public: Boolean = false): Try[Unit] = {
    Try {
      createBucket(destination.bucket)
      if (public) {
        s3.putObject(new PutObjectRequest(destination.bucket, destination.key, file).withCannedAcl(CannedAccessControlList.PublicRead))
      } else {
        s3.putObject(new PutObjectRequest(destination.bucket, destination.key, file))
      }
    }
  }

  def uploadString(destination: S3Object, s: String): Try[Unit] = {
    Try {
      createBucket(destination.bucket)
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

  @deprecated("", since = "v0.13.1")
  def objectExists(address: S3Object, logger: Option[Logger]): Boolean = {

    try {
      val metadata = s3.getObjectMetadata(address.bucket, address.key)
      metadata != null
    } catch {
      case eClient: AmazonClientException => logger.foreach {
        _.warn("object " + address + " is not accessible + " + eClient.getMessage())
      }; false
      case eio: IOException => logger.foreach { _.warn("object " + address + " is not accessible + " + eio.getMessage())}; false
    }
  }

  @deprecated("use generateTemporaryURLLink", since = "v0.13.1")
  def generateTemporaryURL(address: S3Object, time: Int): String = {
    val exp = new java.util.Date()
    var expMs = exp.getTime()
    expMs += 1000 * time
    exp.setTime(expMs)
    s3.generatePresignedUrl(address.bucket, address.key, exp).toString
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
