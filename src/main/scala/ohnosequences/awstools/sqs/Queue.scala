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
    sqs.deleteQueue(new DeleteQueueRequest(url))
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