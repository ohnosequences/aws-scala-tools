package ohnosequences.awstools.s3

import java.io.{InputStream, ByteArrayInputStream, File}

import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials, PropertiesCredentials}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model.{ObjectListing, S3ObjectSummary, ObjectMetadata, PutObjectRequest}

import scala.collection.JavaConversions._
import com.amazonaws.services.importexport.model.NoSuchBucketException


case class ObjectAddress(bucket: String, key: String)

class S3(val s3: AmazonS3) {

  def createBucket(name: String) = {
    if (!s3.doesBucketExist(name)) s3.createBucket(name)
    Bucket(s3, name)
  }

  def getBucket(name: String) = {
    if (s3.doesBucketExist(name)) Some(Bucket(s3, name)) else None
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

  def putObject(objectAddress: ObjectAddress, file: File) {
    s3.putObject(objectAddress.bucket, objectAddress.key, file)
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


//  def listObjects(bucket: String, prefix: String = ""): List[ObjectAddress] = {
//    val result: List[ObjectAddress]
//    var stopped = false
//    while(!stopped) {
//      val listing = s3.listObjects(bucket, prefix)
//      println(listing.getObjectSummaries.map(_.getKey))
//      if(!listing.isTruncated) {
//        stopped = true
//      }
//    }
//  }

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


  //todo next 1000!!!!
  def emptyBucket(name: String) {
    s3.listObjects(name).getObjectSummaries().foreach { objectSummary =>
      s3.deleteObject(objectSummary.getBucketName, objectSummary.getKey)
    }

  }





//  def shutdown() {
//    s3.s
//  }


}

object S3 {

  def create(credentialsFile: File): S3 = {
    create(new PropertiesCredentials(credentialsFile))
  }

  def create(accessKey: String, secretKey: String): S3 = {
    create(new BasicAWSCredentials(accessKey, secretKey))
  }

  def create(credentials: AWSCredentials): S3 = {
    val s3Client = new AmazonS3Client(credentials)
    s3Client.setEndpoint("http://s3-eu-west-1.amazonaws.com")
    new S3(s3Client)
  }
}


