### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
          + s3
            + [Bucket.scala](../s3/Bucket.md)
            + [S3.scala](../s3/S3.md)
          + sns
            + [SNS.scala](../sns/SNS.md)
            + [Topic.scala](../sns/Topic.md)
          + sqs
            + [Queue.scala](Queue.md)
            + [SQS.scala](SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.md)
          + [EC2Tests.scala](../../../../../test/scala/ohnosequences/awstools/EC2Tests.md)
          + [S3Tests.scala](../../../../../test/scala/ohnosequences/awstools/S3Tests.md)
          + [SNSTests.scala](../../../../../test/scala/ohnosequences/awstools/SNSTests.md)
          + [SQSTests.scala](../../../../../test/scala/ohnosequences/awstools/SQSTests.md)

------


```scala
package ohnosequences.awstools.sqs

import java.io.File
import com.amazonaws.services.sqs._
import com.amazonaws.services.sqs.model._
import com.amazonaws.auth._
import scala.collection.JavaConversions._
import com.amazonaws.AmazonServiceException
import com.amazonaws.regions.Regions
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

  def create(provider: AWSCredentialsProvider): SQS = {
    val sqsClient = new AmazonSQSClient(provider)
    sqsClient.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    new SQS(sqsClient)
  }

}




```

