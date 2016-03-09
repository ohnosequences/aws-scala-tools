package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import ohnosequences.awstools.regions._


package object s3 {

  // Just an alias for the "root" S3 fodler:
  def S3Bucket(b: String): S3Folder = S3Folder(b, "")

  implicit def s3AddressFromString(sc: StringContext):
    S3AddressFromString =
    S3AddressFromString(sc)

  implicit def toScalaClient(s3: AmazonS3): ScalaS3Client = ScalaS3Client(s3)

  // Just a convenience constructor
  def client(credentials: AWSCredentialsProvider, region: Region): AmazonS3Client = {
    val javaClient = new AmazonS3Client(credentials)
    javaClient.setRegion(region.toAWSRegion)
    javaClient
  }
}
