package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClient }
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._


package object sqs {

  type MessageId = String

  def client(
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    region: Region,
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSQSClient = {
    new AmazonSQSClient(credentials, configuration)
      .withRegion(region.toAWSRegion)
  }

  // Implicits
  implicit def toScalaClient(sqs: AmazonSQS):
    ScalaSQSClient =
    ScalaSQSClient(sqs)
}
