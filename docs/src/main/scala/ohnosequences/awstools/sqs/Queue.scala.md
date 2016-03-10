
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




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md