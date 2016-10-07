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

  def delete: Try[Unit] = Try { sqs.deleteQueue(queue.url.toString) }

  def purge: Try[Unit] = Try { sqs.purgeQueue(new PurgeQueueRequest(queue.url.toString)) }


  def send(msg: String): Try[MessageId] = Try {
    sqs.sendMessage(queue.url.toString, msg).getMessageId
  }

  def receive(max: Int): Try[Seq[Message]] = Try {

    val response: ReceiveMessageResult = sqs.receiveMessage(
      new ReceiveMessageRequest(queue.url.toString)
        .withMaxNumberOfMessages(max)
    )

    response.getMessages.map { msg => Message(queue, msg) }
  }

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
    sqs.getQueueAttributes(queue.url.toString, Seq(attr))
      .getAttributes.get(attr)
  }

  def arn: String = getAttribute(QueueAttributeName.QueueArn)

  def approxMsgNumber:   Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessages).toInt
  def approxMsgInFlight: Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessagesNotVisible).toInt

  // def setVisibilityTimeout(sec: Int) {
  //   sqs.setQueueAttributes(
  //     new SetQueueAttributesRequest(url, Map("VisibilityTimeout" -> String.valueOf(sec)))
  //   )
  // }

  override def toString = url.toString

}
