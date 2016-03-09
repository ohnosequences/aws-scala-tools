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
import java.net.URI
import java.net.URL


sealed trait AnyS3Address {
  // These are the inputs
  val _bucket: String
  val _key: String

  def toURI: URI = new URI("s3", _bucket, s"/${_key}", null).normalize

  // We use URI as a way to sanitize things (dropping extra /)
  lazy val bucket: String = toURI.getHost
  lazy val key: String = toURI.getPath

  lazy val segments: Seq[String] = key.split("/").filter(_.nonEmpty).toSeq

  @deprecated("Use toURI method instead, or just toString", since = "v0.17.0")
  final def url = "s3://" + bucket + "/" + key

  override def toString = toURI.toString

  def toHttpsURL(region: Region): URL = new URL("https", s"s3-${region}.amazonaws.com", s"${bucket}/${key}")
}

case class S3Folder(b: String, k: String) extends AnyS3Address {
  val _bucket = b
  // NOTE: we explicitly add / in the end here (it represents the empty S3 object of the folder)
  val _key = k + "/"

  def /(suffix: String): S3Object = S3Object(bucket, key + suffix)
}

object S3Folder {

  def apply(uri: URI): S3Folder = S3Folder(uri.getHost, uri.getPath)

  implicit def toS3Object(f: S3Folder): S3Object =
    S3Object(f.bucket, f.key.stripSuffix("/"))
}


case class S3Object(_bucket: String, _key: String) extends AnyS3Address {

  def /(): S3Folder = S3Folder(bucket, key)

  def /(suffix: String): S3Object = this./ / suffix
}

object S3Object {

  def apply(uri: URI): S3Object = S3Object(uri.getHost, uri.getPath)
}


case class S3AddressFromString(val sc: StringContext) extends AnyVal {

  // This allows to write things like s3"bucket" / "foo" / "bar" /
  // or s3"org.com/${suffix}/${folder.getName}" / "file.foo"
  def s3(args: Any*): S3Folder = {
    val str = sc.s(args: _*)
    S3Folder(new URI("s3://" + str))
  }
}



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
