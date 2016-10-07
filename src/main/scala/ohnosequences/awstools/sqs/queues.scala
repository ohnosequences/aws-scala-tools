package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.model.{ Message => AmazonMessage, _ }
import com.amazonaws.services.sqs.AmazonSQS
import scala.concurrent.duration._
import scala.collection.JavaConversions._
import java.net.URL
import scala.util.Try


/* Amazon Java SDK doesn't have an explicit abstraction for SQS queues */
case class Queue(
  val sqs: AmazonSQS,
  val url: URL
) { queue =>

  def delete(): Try[Unit] = Try { sqs.deleteQueue(queue.url.toString) }

  def purge(): Try[Unit] = Try { sqs.purgeQueue(new PurgeQueueRequest(queue.url.toString)) }


  def send(msg: String): Try[MessageId] = Try {
    sqs.sendMessage(queue.url.toString, msg).getMessageId
  }

  /* Batch and in parallel. Note that there is a total limit on the batch size: 256KiB. */
  def sendBatch(msgs: Iterator[String]): (Seq[MessageId], Seq[(String, BatchResultErrorEntry)]) =
    // TODO: check messages lenghts not to exceed the total batch size limit
    msgs.grouped(10).foldLeft(
      (Seq[MessageId](), Seq[(String, BatchResultErrorEntry)]())
    ) { case ((succeses, failures), group) =>

      val batch = group.zipWithIndex.map { case (msg, ix) =>
        new SendMessageBatchRequestEntry(ix.toString, msg)
      }
      val response: SendMessageBatchResult = sqs.sendMessageBatch(queue.url.toString, batch)
      (
        response.getSuccessful.map { _.getMessageId } ++ succeses,
        response.getFailed.map { err => (group.apply(err.getId.toInt), err) } ++ failures
      )
    }

  def receive(max: Int): Try[Seq[Message]] = Try {

    val response: ReceiveMessageResult = sqs.receiveMessage(
      new ReceiveMessageRequest(queue.url.toString)
        .withMaxNumberOfMessages(max)
    )

    response.getMessages.map { msg => Message(queue, msg) }
  }

  def receive(): Try[Message] = receive(1).map { _.head }


  def poll(
    responseWaitTime: Option[Integer]            = None,
    additionalVisibilityTimeout: Option[Integer] = None,
    pollingDeadline: Option[Deadline]            = None,
    iterationSleep: Duration                     = 300.millis,
    maxSequentialEmptyResonses: Integer          = 5,
    maxMessages: Option[Integer]                 = None
  ): Seq[Message] = {

    val request = {
      val r1 = new ReceiveMessageRequest(queue.url.toString)
        .withMaxNumberOfMessages(10)
      val r2 = additionalVisibilityTimeout.map { r1.withVisibilityTimeout }.getOrElse(r1)
      val r3 = responseWaitTime.map { r2.withWaitTimeSeconds }.getOrElse(r2)
      r3
    }

    def deadlineHasCome: Boolean = pollingDeadline.map(_.isOverdue).getOrElse(false)

    def gotEnough[M](msgs: Iterable[M]): Boolean = maxMessages.map{ _ < msgs.size }.getOrElse(false)

    def wrapResult(msgs: Iterable[AmazonMessage]): Seq[Message] = msgs.toSeq.map(Message(queue, _))

    @scala.annotation.tailrec
    def poll_rec(
      acc: scala.collection.mutable.Map[String, AmazonMessage],
      emptyResponses: Int
    ): Seq[Message] = {

      Thread.sleep(iterationSleep.toMillis)

      if (deadlineHasCome || gotEnough(acc)) wrapResult(acc.values)
      else {
        val response = sqs.receiveMessage(request).getMessages.map { msg =>
          msg.getMessageId -> msg
        }

        if (response.isEmpty) {
          if (emptyResponses > maxSequentialEmptyResonses) wrapResult(acc.values)
          else poll_rec(acc ++= response, emptyResponses + 1)
        } else
          poll_rec(acc ++= response, 0)
      }
    }

    poll_rec(scala.collection.mutable.Map(), 0)
  }


  /* Providing read access to some useful attributes */
  def getAttribute(attr: QueueAttributeName): String = {
    // NOTE: the only exception it throws is about invalid name of the attribute, but we control it by using the QueueAttributeName enum instead of just String.
    sqs.getQueueAttributes(queue.url.toString, List(attr.toString))
      .getAttributes.get(attr.toString)
  }

  // This shouldn't change over time, so I make it a lazy val:
  lazy val arn: String = getAttribute(QueueAttributeName.QueueArn)

  def approxMsgAvailable: Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessages).toInt
  def approxMsgInFlight:  Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessagesNotVisible).toInt
  def approxMsgTotal:     Int = approxMsgAvailable + approxMsgInFlight

  def visibilityTimeout: Duration = getAttribute(QueueAttributeName.VisibilityTimeout).toInt.seconds


  /* A shortcut for setting attributes */
  def setAttribute(attr: QueueAttributeName, value: String): Try[Unit] = Try {
    sqs.setQueueAttributes(queue.url.toString, Map(attr.toString -> value))
  }

  /* Note that visibility timeout cannot be more than 12 hours */
  def setVisibilityTimeout(seconds: Int): Try[Unit] = setAttribute(QueueAttributeName.VisibilityTimeout, seconds.toString)

  // TODO: get/set for MessageRetentionPeriod, ReceiveMessageWaitTimeSeconds, etc.


  override def toString = url.toString
}
