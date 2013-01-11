package awsaws.sqs

import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.AmazonSQS
import scala.collection.JavaConversions._

case class Queue(val sqs: AmazonSQS, val url: String, val name: String) {

  def sendMessage(message: String) = sqs.sendMessage(new SendMessageRequest(url, message))

  def receiveMessages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(10)).getMessages

  def receiveMessage: Option[Message] = {
    val messages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(1)).getMessages
    if (messages.isEmpty) {
      None
    } else {
      Some(messages.get(0))
    }
  }

  def receiveAndDeleteMessages = {
    val messages = receiveMessages
    for (message <- messages) {
      deleteMessage(message)
    }
    messages
  }

  def deleteMessage(message: Message) {
    sqs.deleteMessage(new DeleteMessageRequest(url, message.getReceiptHandle))
  }

  def delete() {
    sqs.deleteQueue(new DeleteQueueRequest(url))
  }

  def getAttribute(name: QueueAttributeName) = sqs.getQueueAttributes(
    new GetQueueAttributesRequest(url).withAttributeNames(name.toString)
  ).getAttributes.get(name.toString)

  def getArn = getAttribute(QueueAttributeName.QueueArn)

  def getApproximateNumberOfMessages = getAttribute(QueueAttributeName.ApproximateNumberOfMessages)

  def setVisibilityTimeout(sec: Int) {
    sqs.setQueueAttributes(
      new SetQueueAttributesRequest(url, Map("VisibilityTimeout" -> String.valueOf(sec)))
    )
  }

  def setAttributes(attributes: Map[String, String]) {
    sqs.setQueueAttributes(new SetQueueAttributesRequest(url, attributes))
  }

  override def toString = {
    "[ name=" + name + "; " + "url=" + url + "; " + " messages = " + getApproximateNumberOfMessages + " ]"
  }



}