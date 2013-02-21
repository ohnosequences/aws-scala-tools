package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.model._
import com.amazonaws.services.sqs.AmazonSQS
import scala.collection.JavaConversions._

case class Message(body: String, receiptHandle: String)

case class Queue(val sqs: AmazonSQS, val url: String, val name: String) {

  def sendMessage(message: String) {
    sqs.sendMessage(new SendMessageRequest(url, message))
  }

  //def receiveMessages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(10)).getMessages

  def receiveMessage: Option[ohnosequences.awstools.sqs.Message] = {
    val messages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(1)).getMessages
    if (messages.isEmpty) {
      None
    } else {
      val message = messages.get(0)
      Some(ohnosequences.awstools.sqs.Message(message.getBody, message.getReceiptHandle))
    }
  }

  def receiveMessages(amount: Int): List[ohnosequences.awstools.sqs.Message] = {
    val messages = sqs.receiveMessage(new ReceiveMessageRequest(url).withMaxNumberOfMessages(amount)).getMessages
    if (messages.isEmpty) {
      List[ohnosequences.awstools.sqs.Message]()
    } else {
      messages.map(message => ohnosequences.awstools.sqs.Message(message.getBody, message.getReceiptHandle)).toList
    }
  }

//  def receiveAndDeleteMessages = {
//    val messages = receiveMessages
//    for (message <- messages) {
//      deleteMessage(message)
//    }
//    messages
//  }

  def deleteMessage(message: ohnosequences.awstools.sqs.Message) {
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