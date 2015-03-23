
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


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]
            + [AutoScalingGroup.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]
          + [AWSClients.scala][main\scala\ohnosequences\awstools\AWSClients.scala]
          + dynamodb
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]
          + ec2
            + [EC2.scala][main\scala\ohnosequences\awstools\ec2\EC2.scala]
            + [Filters.scala][main\scala\ohnosequences\awstools\ec2\Filters.scala]
            + [InstanceType.scala][main\scala\ohnosequences\awstools\ec2\InstanceType.scala]
            + [Utils.scala][main\scala\ohnosequences\awstools\ec2\Utils.scala]
          + regions
            + [Region.scala][main\scala\ohnosequences\awstools\regions\Region.scala]
          + s3
            + [Bucket.scala][main\scala\ohnosequences\awstools\s3\Bucket.scala]
            + [S3.scala][main\scala\ohnosequences\awstools\s3\S3.scala]
          + sns
            + [SNS.scala][main\scala\ohnosequences\awstools\sns\SNS.scala]
            + [Topic.scala][main\scala\ohnosequences\awstools\sns\Topic.scala]
          + sqs
            + [Queue.scala][main\scala\ohnosequences\awstools\sqs\Queue.scala]
            + [SQS.scala][main\scala\ohnosequences\awstools\sqs\SQS.scala]
          + utils
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]
            + [SQSUtils.scala][main\scala\ohnosequences\awstools\utils\SQSUtils.scala]
        + benchmark
          + [Benchmark.scala][main\scala\ohnosequences\benchmark\Benchmark.scala]
        + logging
          + [Logger.scala][main\scala\ohnosequences\logging\Logger.scala]
          + [S3Logger.scala][main\scala\ohnosequences\logging\S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [EC2Tests.scala][test\scala\ohnosequences\awstools\EC2Tests.scala]
          + [InstanceTypeTests.scala][test\scala\ohnosequences\awstools\InstanceTypeTests.scala]
          + [RegionTests.scala][test\scala\ohnosequences\awstools\RegionTests.scala]
          + [S3Tests.scala][test\scala\ohnosequences\awstools\S3Tests.scala]
          + [SQSTests.scala][test\scala\ohnosequences\awstools\SQSTests.scala]
          + [TestCredentials.scala][test\scala\ohnosequences\awstools\TestCredentials.scala]

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md