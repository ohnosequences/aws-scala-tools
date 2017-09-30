package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.s3.{ AmazonS3, AmazonS3ClientBuilder }
import com.amazonaws.services.s3.transfer.TransferManager
import com.amazonaws.{ ClientConfiguration, PredefinedClientConfigurations }
import ohnosequences.awstools.regions._


package object s3 {

  // Just an alias for the "root" S3 fodler:
  def S3Bucket(b: String): S3Folder = S3Folder(b, "")

  def clientBuilder: AmazonS3ClientBuilder =
    AmazonS3ClientBuilder.standard()

  def defaultClient: AmazonS3 =
    AmazonS3ClientBuilder.defaultClient()

  @deprecated("Use s3.clientBuilder or s3.defaultClient instead", since = "0.19.0")
  def S3Client(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonS3 = {
    clientBuilder
      .withCredentials(credentials)
      .withClientConfiguration(configuration)
      .withRegion(region.getName)
      .build()
  }

  // Implicits
  implicit def s3AddressFromString(sc: StringContext):
    S3AddressFromString =
    S3AddressFromString(sc)

  implicit def toScalaS3Client(s3: AmazonS3):
    ScalaS3Client =
    ScalaS3Client(s3)

  implicit def toScalaTransferManager(tm: TransferManager):
    ScalaTransferManager =
    ScalaTransferManager(tm)
}
