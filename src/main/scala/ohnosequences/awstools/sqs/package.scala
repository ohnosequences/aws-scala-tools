package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sqs.{ AmazonSQS, AmazonSQSClient }
// import com.amazonaws.services.sqs.model._
import ohnosequences.awstools.regions._


package object sqs {

  type MessageId = String

  def client(
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    region: Region
  ): AmazonSQSClient = {
    val javaClient = new AmazonSQSClient(credentials)
    javaClient.setRegion(region.toAWSRegion)
    javaClient
  }

  // Implicits
  implicit def toScalaClient(sqs: AmazonSQS):
    ScalaSQSClient =
    ScalaSQSClient(sqs)

  // implicit def fromSendMessageResult(result: SendMessageResult): Message =
  //   new Message
  //     .withMessageId(result.getMessageId)
}
