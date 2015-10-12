
```scala
package ohnosequences.awstools.utils


import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._

case class SQSQueueInfo(url: String, approx: String, inFlight: String)

object SQSUtils {

  @tailrec
  def receiveMessage(sqs: AmazonSQS, queueUrl: String): Try[Message] = {
    Try {
      val res = sqs.receiveMessage(new ReceiveMessageRequest()
        .withQueueUrl(queueUrl)
        .withWaitTimeSeconds(20) //max
        .withMaxNumberOfMessages(1)
        // .withAttributeNames(DynamoDBQueue.idAttr)
      )
      res.getMessages.headOption
    } match {
      case Failure(f) => Failure[Message](f)
      case Success(None) => {
        receiveMessage(sqs, queueUrl)
      }
      case Success(Some(message)) => Success(message)
    }
  }

  //todo idempotent????
  def deleteMessage(sqs: AmazonSQS, queueUrl: String, handle: String): Try[Unit] = {
    Try {
      sqs.deleteMessage(queueUrl, handle)
    }
  }

  def writeBatch(sqs: AmazonSQS, queueUrl: String, items: List[String]): Try[Unit] = {
    val rawItems = items.zipWithIndex.map { case (item, i) =>
      new SendMessageBatchRequestEntry(i.toString, item)
    }

    writeBatchRaw(sqs, queueUrl, rawItems)

  }

  def getSQSInfo(sqs: AmazonSQS, sqsUrl: String): Try[SQSQueueInfo] = {
    Success(()).flatMap { u =>
      val attributes = List(QueueAttributeName.ApproximateNumberOfMessages, QueueAttributeName.ApproximateNumberOfMessagesNotVisible).map(_.toString)
      Option(sqs.getQueueAttributes(sqsUrl, attributes).getAttributes) match {
        case Some(attrs) if attributes.forall(attrs.containsKey(_)) => {
          Success(SQSQueueInfo(
            url = sqsUrl,
            approx = attrs.get(QueueAttributeName.ApproximateNumberOfMessages.toString),
            inFlight = attrs.get(QueueAttributeName.ApproximateNumberOfMessagesNotVisible.toString)
          ))
        }
        case _ => Failure(new Error("failed to retrieve queue attributes"))
      }
    }
  }


  @tailrec
  def writeBatchRaw(sqs: AmazonSQS, queueUrl: String, items: List[SendMessageBatchRequestEntry]): Try[Unit] = {
    if (items == null | items.isEmpty) {
      Success(())
    } else {
      val (left, right) = items.splitAt(10)
      Try {
        sqs.sendMessageBatch(new SendMessageBatchRequest()
          .withQueueUrl(queueUrl)
          .withEntries(left)
        )
        ()
      } match {
        case Failure(f) => Failure(f)
        case Success(_) => {
          writeBatchRaw(sqs, queueUrl, right)
        }
      }
    }
  }

  def deleteQueue(sqs: AmazonSQS, sqsUrl: String): Try[Unit] = {
    Try {
      sqs.deleteQueue(sqsUrl)
    }
  }
}

//  .withEntries(left.zipWithIndex.map { case (item, i) =>
//item.makeSQSEntry(i + 1)
//})
```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md