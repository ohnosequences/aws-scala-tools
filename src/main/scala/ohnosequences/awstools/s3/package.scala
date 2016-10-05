package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3Client }
import com.amazonaws.services.s3.transfer.TransferManager
import ohnosequences.awstools.regions._


package object s3 {

  // Just an alias for the "root" S3 fodler:
  def S3Bucket(b: String): S3Folder = S3Folder(b, "")

  def client(
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    region: Region
  ): AmazonS3Client = {
    val javaClient = new AmazonS3Client(credentials)
    javaClient.setRegion(region.toAWSRegion)
    javaClient
  }

  // Implicits
  implicit def s3AddressFromString(sc: StringContext):
    S3AddressFromString =
    S3AddressFromString(sc)

  implicit def toScalaClient(s3: AmazonS3):
    ScalaS3Client =
    ScalaS3Client(s3)

  implicit def transferManagerOps(tm: TransferManager):
    TransferManagerOps =
    TransferManagerOps(tm)
}
