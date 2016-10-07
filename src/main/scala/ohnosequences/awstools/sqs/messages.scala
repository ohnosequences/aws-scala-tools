package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs, sqs.AmazonSQS, sqs.model._
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.util.Try
import java.net.URL


/* This is a wrapper for an instance of an SQS message. You can get it with `Queue#receiveMessage`. */
case class Message(
  val queue: Queue,
  val asJava: sqs.model.Message
) { message =>

  /* Unique message identifier. You get this when you send a message, but you can't use it to _refer_ to messages. */
  def id: MessageId = asJava.getMessageId()
  /* A handle you get for each instance of a _received_ message. You need it to delete a message or change its visibility timeout. */
  def receiptHandle: String = asJava.getReceiptHandle()

  def body: String = asJava.getBody()

  /* Note that to message will be deleted even if it's locked by the visibility timeout. */
  def delete(): Try[Unit] = Try {
    queue.sqs.deleteMessage(
      queue.url.toString,
      message.receiptHandle
    )
  }

  /* Note that the total visibility time for a message is 12 hours. See more details in the [Visibility Timeout](http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/AboutVT.html) Amazon docs.
  */
  def changeVisibility(additionalSeconds: Integer): Try[Unit] = Try {
    queue.sqs.changeMessageVisibility(
      queue.url.toString,
      message.receiptHandle,
      additionalSeconds
    )
  }

  override def toString = Map(
    "id" -> message.id,
    // "receiptHandle" -> message.receiptHandle,
    "body" -> message.body
  ).toString
}
