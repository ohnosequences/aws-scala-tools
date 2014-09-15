package ohnosequences.awstools.sqs

import java.io.File

import scala.collection.JavaConversions._

import ohnosequences.awstools.regions.Region._

import com.amazonaws.services.sqs._
import com.amazonaws.services.sqs.model._
import com.amazonaws.auth._
import com.amazonaws.AmazonServiceException
import com.amazonaws.internal.StaticCredentialsProvider


class SQS(val sqs: AmazonSQS) {

  def createQueue(name: String) = Queue(sqs = sqs, url = sqs.createQueue(new CreateQueueRequest(name)).getQueueUrl, name = name)

  def getQueueByName(name: String) = {
    try {
      val response = sqs.getQueueUrl(new GetQueueUrlRequest(name))
      Some(Queue(sqs =sqs, response.getQueueUrl, name = name))
    } catch {
      case e: AmazonServiceException if e.getStatusCode == 400 => None
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

  def create(): SQS = {
    create(new InstanceProfileCredentialsProvider())
  }

  def create(credentialsFile: File): SQS = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)))
  }

  def create(accessKey: String, secretKey: String): SQS = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
  }

  def create(provider: AWSCredentialsProvider, region: ohnosequences.awstools.regions.Region = Ireland): SQS = {
    val sqsClient = new AmazonSQSClient(provider)
    sqsClient.setRegion(region)
    new SQS(sqsClient)
  }

}



