package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClient }
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._


package object sqs {

  type MessageId = String

  def SQSClient(
    region: Region,
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSQSClient = {
    new AmazonSQSClient(credentials, configuration)
      .withRegion(region.toAWSRegion)
  }

  // Implicits
  implicit def toScalaSQSClient(sqs: AmazonSQS):
    ScalaSQSClient =
    ScalaSQSClient(sqs)
}
