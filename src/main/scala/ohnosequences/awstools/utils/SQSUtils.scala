package ohnosequences.awstools.utils


import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.{SendMessageBatchRequestEntry, Message, ReceiveMessageRequest, SendMessageBatchRequest}

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._


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