### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
          + regions
            + [Region.scala](../regions/Region.md)
          + s3
            + [Bucket.scala](../s3/Bucket.md)
            + [S3.scala](../s3/S3.md)
          + sns
            + [SNS.scala](../sns/SNS.md)
            + [Topic.scala](../sns/Topic.md)
          + sqs
            + [Queue.scala](Queue.md)
            + [SQS.scala](SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.md)
          + [EC2Tests.scala](../../../../../test/scala/ohnosequences/awstools/EC2Tests.md)
          + [S3Tests.scala](../../../../../test/scala/ohnosequences/awstools/S3Tests.md)
          + [SNSTests.scala](../../../../../test/scala/ohnosequences/awstools/SNSTests.md)
          + [SQSTests.scala](../../../../../test/scala/ohnosequences/awstools/SQSTests.md)

------


```scala
package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.AmazonSQS
import scala.collection.JavaConversions._


case class Message(sqs: AmazonSQS, url: String, body: String, receiptHandle: String) {
  def changeVisibilityTimeout(additionalSecs: Int) {
    sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest()
      .withQueueUrl(url)
      .withReceiptHandle(receiptHandle)
      .withVisibilityTimeout(additionalSecs)
    )
  }
}

case class Queue(sqs: AmazonSQS, url: String, name: String) {



  def sendMessage(message: String) {
    sqs.sendMessage(new SendMessageRequest(url, message))
  }

  def receiveMessage: Option[Message] = {
    val messages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(1)).getMessages
    if (messages.isEmpty) {
      None
    } else {
      val message = messages.get(0)
      Some(Message(sqs, url, message.getBody, message.getReceiptHandle))
    }
  }

  def receiveMessages(amount: Int): List[Message] = {
    val messages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(amount)).getMessages
    if (messages.isEmpty) {
      List[Message]()
    } else {
      messages.map(message => Message(sqs, url, message.getBody, message.getReceiptHandle)).toList
    }
  }


  def deleteMessage(message: Message) {
    sqs.deleteMessage(new DeleteMessageRequest(url, message.receiptHandle))
  }

  def delete() {
    try {
      sqs.deleteQueue(new DeleteQueueRequest(url))
    } catch {
      case t: Throwable => println("error during topic queue " + url + " : " + t.getMessage); t.printStackTrace()
    }

  }

  def getAttribute(name: QueueAttributeName) = sqs.getQueueAttributes(
    new GetQueueAttributesRequest(url).withAttributeNames(name.toString)
  ).getAttributes.get(name.toString)

  def getAllAttributes = sqs.getQueueAttributes(
    new GetQueueAttributesRequest(url).withAttributeNames(List("All"))
  ).getAttributes.toMap

  def getArn = getAttribute(QueueAttributeName.QueueArn)

  def getApproximateNumberOfMessages = getAttribute(QueueAttributeName.ApproximateNumberOfMessages).toInt

  def setVisibilityTimeout(sec: Int) {
    sqs.setQueueAttributes(
      new SetQueueAttributesRequest(url, Map("VisibilityTimeout" -> String.valueOf(sec)))
    )
  }

  def setAttributes(attributes: Map[String, String]) {
    sqs.setQueueAttributes(new SetQueueAttributesRequest(url, attributes))
  }

  override def toString = {
    name
    //"[ name=" + name + "; " + "url=" + url + "; " + " messages = " + getApproximateNumberOfMessages + " ]"
  }

}
```

