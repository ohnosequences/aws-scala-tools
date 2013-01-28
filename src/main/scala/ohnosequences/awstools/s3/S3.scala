package ohnosequences.awstools.s3

import java.io.{ByteArrayInputStream, File}

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.s3.model.{ObjectMetadata, PutObjectRequest}

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

  def putWholeObject(objectAddress: ObjectAddress, content: String) {
    val array = content.getBytes

    val stream = new ByteArrayInputStream(array)
    val metadata = new ObjectMetadata()
    metadata.setContentLength(array.length)
    s3.putObject(objectAddress.bucket, objectAddress.key, stream, metadata)
  }

  def deleteObject(objectAddress: ObjectAddress) {
    s3.deleteObject(objectAddress.bucket, objectAddress.key)
  }


//  def shutdown() {
//    s3.s
//  }


}

object S3 {
  def create(credentialsFile: File): S3 = {
    val s3Client = new AmazonS3Client(new PropertiesCredentials(credentialsFile))
    s3Client.setEndpoint("http://s3-eu-west-1.amazonaws.com")
    new S3(s3Client)
  }
}


