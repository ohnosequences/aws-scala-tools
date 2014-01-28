package ohnosequences.awstools.s3

import java.io.{InputStream, ByteArrayInputStream, File}

import ohnosequences.awstools.regions.Region._

import com.amazonaws.auth._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model._

import scala.collection.JavaConversions._
import com.amazonaws.services.importexport.model.NoSuchBucketException
import com.amazonaws.services.s3.transfer.{Transfer, TransferManager}

import com.amazonaws.AmazonServiceException
import scala.collection.mutable.ListBuffer
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.event._
import com.amazonaws.event.{ProgressListener => PListener, ProgressEvent => PEvent}



case class ObjectAddress(bucket: String, key: String)

case class TransferListener(transfer: Transfer) extends PListener {
  def progressChanged(progressEvent: PEvent) { 
    import PEvent._
    progressEvent.getEventCode() match {
      case STARTED_EVENT_CODE  => println("Started")
      case CANCELED_EVENT_CODE  => println("Canceled!")
      case COMPLETED_EVENT_CODE  => println("Completed!")
      case FAILED_EVENT_CODE  => println("Failed!")
      case PART_COMPLETED_EVENT_CODE  => println("Completed part: "+ transfer.getProgress.getBytesTransferred)
      // case PART_FAILED_EVENT_CODE  => println("")
      // case PART_STARTED_EVENT_CODE  => println("")
      // case PREPARING_EVENT_CODE  => println("")
      // case RESET_EVENT_CODE  => println("")
      case _ => ()
    }
  }
}

case class LoadingManager(transferManager: TransferManager) {

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
    objectAddress: ObjectAddress, 
    file: File, 
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Uploading to: " + objectAddress.toString)
    println("File: " + file.getAbsolutePath)
    val upload = transferManager.upload(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(upload)
  }

  def uploadDirectory(
    objectAddress: ObjectAddress, 
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
    objectAddress: ObjectAddress, 
    file: File, 
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Dowloading from: " + objectAddress.toString)
    println("File: " + file.getAbsolutePath)
    val download = transferManager.download(objectAddress.bucket, objectAddress.key, file)
    transferWaiter(download)
  }

  def downloadDirectory(
    objectAddress: ObjectAddress, 
    destinationDir: File, 
    transferWaiter: (Transfer => Unit) = transferWaiter
  ) {
    println("Dowloading from: " + objectAddress.toString)
    println("To directory: " + destinationDir.getAbsolutePath)
    val download = transferManager.downloadDirectory(objectAddress.bucket, objectAddress.key, destinationDir)
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
    s3Client.setRegion(Ireland)
    new S3(s3Client)
  }
}


