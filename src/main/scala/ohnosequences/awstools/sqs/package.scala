package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClientBuilder }
import com.amazonaws.{ ClientConfiguration, PredefinedClientConfigurations }
import ohnosequences.awstools.regions._


package object sqs {

  type MessageId = String

  def clientBuilder: AmazonSQSClientBuilder =
    AmazonSQSClientBuilder.standard()

  def defaultClient: AmazonSQS =
    AmazonSQSClientBuilder.defaultClient()

  @deprecated("Use sqs.clientBuilder or sqs.defaultClient instead", since = "0.19.0")
  def SQSClient(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSQS = {
    clientBuilder
      .withCredentials(credentials)
      .withClientConfiguration(configuration)
      .withRegion(region.getName)
      .build()
  }

  // Implicits
  implicit def toScalaSQSClient(sqs: AmazonSQS):
    ScalaSQSClient =
    ScalaSQSClient(sqs)
}
