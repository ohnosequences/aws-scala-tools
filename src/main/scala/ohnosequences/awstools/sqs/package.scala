package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClient }
import com.amazonaws.{ ClientConfiguration, PredefinedClientConfigurations }
import ohnosequences.awstools.regions._


package object sqs {

  type MessageId = String

  def SQSClient(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSQSClient = {
    new AmazonSQSClient(credentials, configuration)
      .withRegion(region)
  }

  // Implicits
  implicit def toScalaSQSClient(sqs: AmazonSQS):
    ScalaSQSClient =
    ScalaSQSClient(sqs)
}
