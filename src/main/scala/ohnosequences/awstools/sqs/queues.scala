package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.AmazonSQS
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

  def receive(max: Int): Try[Seq[Message]] = Try {

    val response: ReceiveMessageResult = sqs.receiveMessage(
      new ReceiveMessageRequest(queue.url.toString)
        .withMaxNumberOfMessages(max)
    )

    response.getMessages.map { msg => Message(queue, msg) }
  }

  // TODO: polling
  // def shortPoll = ???
  // def longPoll = ???

  // TODO: these attributes seem to be useful

  // def getApproximateNumberOfMessages = getAttribute(QueueAttributeName.ApproximateNumberOfMessages).toInt

  // def setVisibilityTimeout(sec: Int) {
  //   sqs.setQueueAttributes(
  //     new SetQueueAttributesRequest(url, Map("VisibilityTimeout" -> String.valueOf(sec)))
  //   )
  // }

  override def toString = url.toString

}
