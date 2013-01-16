package ohnosequences.awstools.sqs

import java.io.File
import com.amazonaws.services.sqs._
import com.amazonaws.services.sqs.model._
import com.amazonaws.auth.PropertiesCredentials
import scala.collection.JavaConversions._
import com.amazonaws.AmazonServiceException


class SQS(val sqs: AmazonSQS) {

  def createQueue(name: String) = Queue(sqs = sqs, url = sqs.createQueue(new CreateQueueRequest(name)).getQueueUrl, name = name)

  def getQueueByName(name: String) = {
    try {
      val response = sqs.getQueueUrl(new GetQueueUrlRequest(name))
      Some(Queue(sqs, response.getQueueUrl, name = name))
    } catch {
      case e: AmazonServiceException if (e.getStatusCode == 400) => None
    }
  }

  def printQueues() {
    for (queueUrl <- sqs.listQueues().getQueueUrls) {
      println("queueUrl: " + queueUrl)
    }
  }

  def shutdown() {
    sqs.shutdown()
  }

}

object SQS {

  def create(credentialsFile: File): SQS = {
    val sqsClient = new AmazonSQSClient(new PropertiesCredentials(credentialsFile))
    sqsClient.setEndpoint("http://sqs.eu-west-1.amazonaws.com")
    new SQS(sqsClient)
  }
}



