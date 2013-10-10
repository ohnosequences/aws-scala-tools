package ohnosequences.awstools.s3

import java.io.{InputStream, ByteArrayInputStream, File}

import com.amazonaws.auth._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model._

import scala.collection.JavaConversions._
import com.amazonaws.services.importexport.model.NoSuchBucketException
import com.amazonaws.services.s3.transfer.{Transfer, TransferManager}

import com.amazonaws.AmazonServiceException
import scala.collection.mutable.ListBuffer
import com.amazonaws.regions.Regions
import com.amazonaws.internal.StaticCredentialsProvider



case class ObjectAddress(bucket: String, key: String)

case class LoadingManager(transferManager: TransferManager) {

  val transferWaiter: (Transfer => Unit) = {
    transfer =>
      while(!transfer.isDone)   {
        println("Transfer: " + transfer.getDescription)
        println("  - State: " + transfer.getState)
        println("  - Progress: " +   transfer.getProgress.getBytesTransferred)
        // Do work while we wait for our upload to complete...
        Thread.sleep(500)
      }
  }

  def upload(objectAddress: ObjectAddress, file: File, transferWaiter: (Transfer => Unit) = transferWaiter) {
    val upload = transferManager.upload(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(upload)

  }

  def download(objectAddress: ObjectAddress, file: File, transferWaiter: (Transfer => Unit) = transferWaiter) {
    val download = transferManager.download(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(download)
  }

  //def shutdown() {
    //transferManager.shutdownNow()
  //}
}

class S3(val s3: AmazonS3) {

  def createLoadingManager(): LoadingManager = new LoadingManager(new TransferManager(s3))

  def tryAction[T](action: () => Option[T], attemptsLeft: Int = 10, timeOut: Int = 500): Option[T] = {
    if(attemptsLeft == 0) {
      None
    } else {
      action() match {
        case Some(t) => Some(t)
        case None => Thread.sleep(timeOut); tryAction(action, attemptsLeft - 1)
      }
    }
  }

  def createBucket(name: String) = {
    val createBucketAction: () => Option[Boolean] = {  () =>
      try {
        s3.createBucket(name)
        Some(true)
      } catch {
        case e: AmazonServiceException if e.getStatusCode == 409 => Some(true)
        case e: AmazonServiceException => println("warning: " + e.toString); None
      }
    }
    tryAction(createBucketAction)
    Bucket(s3, name)
  }

  def getBucket(name: String) = {
    if (s3.doesBucketExist(name)) Some(Bucket(s3, name)) else None
  }

  def copy(src: ObjectAddress, dst: ObjectAddress): Boolean = {
    try {

      s3.copyObject(src.bucket, src.key, dst.bucket, dst.key)
      true
    } catch {
      case t: Throwable => t.printStackTrace(); false
    }

  }

  def readWholeObject(objectAddress: ObjectAddress) = {
    val objectStream = s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
    scala.io.Source.fromInputStream(objectStream).mkString
  }

  def readObject(objectAddress: ObjectAddress): Option[String] = {
    try {
      val objectStream = s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
      Some(scala.io.Source.fromInputStream(objectStream).mkString)
    } catch {
      case t: Throwable => None
    }
  }

  def getObjectStream(objectAddress: ObjectAddress): InputStream = {
    s3.getObject(objectAddress.bucket, objectAddress.key).getObjectContent
  }

  def putWholeObject(objectAddress: ObjectAddress, content: String) {
    val array = content.getBytes

    val stream = new ByteArrayInputStream(array)
    val metadata = new ObjectMetadata()
    metadata.setContentLength(array.length)
    s3.putObject(objectAddress.bucket, objectAddress.key, stream, metadata)
  }

  def putObject(objectAddress: ObjectAddress, file: File, public: Boolean = false) {
    createBucket(objectAddress.bucket)

    if (public) {
      s3.putObject(new PutObjectRequest(objectAddress.bucket, objectAddress.key, file).withCannedAcl(CannedAccessControlList.PublicRead))
    } else {
      s3.putObject(new PutObjectRequest(objectAddress.bucket, objectAddress.key, file))
    }
  }


  def deleteObject(objectAddress: ObjectAddress) {
    s3.deleteObject(objectAddress.bucket, objectAddress.key)
  }

  def deleteBucket(name: String, empty: Boolean = true) {
    if (s3.doesBucketExist(name)) {
      if (empty) {
        emptyBucket(name)
      }
      try {
      s3.deleteBucket(name)
      } catch {
        case e: NoSuchBucketException => ()
      }
    }
  }


  def listObjects(bucket: String, prefix: String = ""): List[ObjectAddress] = {
    val result = ListBuffer[ObjectAddress]()
    var stopped = false
    while(!stopped) {
      val listing = s3.listObjects(bucket, prefix)

      result ++= listing.getObjectSummaries.map{ summary =>
        ObjectAddress(bucket, summary.getKey)
      }

      if(!listing.isTruncated) {
        stopped = true
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

  def create(credentials: AWSCredentialsProvider): S3 = {
    val s3Client = new AmazonS3Client(credentials)
    s3Client.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    new S3(s3Client)
  }
}


