package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sns.{ AmazonSNS, AmazonSNSClientBuilder }
import com.amazonaws.{ ClientConfiguration, PredefinedClientConfigurations }
import ohnosequences.awstools.regions._

package object sns {

  def clientBuilder: AmazonSNSClientBuilder =
    AmazonSNSClientBuilder.standard()

  def defaultClient: AmazonSNS =
    AmazonSNSClientBuilder.defaultClient()

  @deprecated("Use sns.clientBuilder or sns.defaultClient instead", since = "0.19.0")
  def SNSClient(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSNS = {
    clientBuilder
      .withCredentials(credentials)
      .withClientConfiguration(configuration)
      .withRegion(region.getName)
      .build()
  }

  // Implicits
  implicit def toScalaSNSClient(sns: AmazonSNS):
    ScalaSNSClient =
    ScalaSNSClient(sns)
}
