package ohnosequences.awstools.s3

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}

class S3(val s3: AmazonS3) {

  def createBucket(name: String) = {
    if (!s3.doesBucketExist(name)) s3.createBucket(name)
    Bucket(s3, name)
  }

  def getBucket(name: String) = {
    if (s3.doesBucketExist(name)) Some(Bucket(s3, name)) else None
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
