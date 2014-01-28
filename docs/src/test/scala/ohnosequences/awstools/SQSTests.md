### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../../../../main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../../../../main/scala/ohnosequences/awstools/ec2/EC2.md)
            + [Filters.scala](../../../../main/scala/ohnosequences/awstools/ec2/Filters.md)
            + [InstanceType.scala](../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.md)
            + [Utils.scala](../../../../main/scala/ohnosequences/awstools/ec2/Utils.md)
          + regions
            + [Region.scala](../../../../main/scala/ohnosequences/awstools/regions/Region.md)
          + s3
            + [Bucket.scala](../../../../main/scala/ohnosequences/awstools/s3/Bucket.md)
            + [S3.scala](../../../../main/scala/ohnosequences/awstools/s3/S3.md)
          + sns
            + [SNS.scala](../../../../main/scala/ohnosequences/awstools/sns/SNS.md)
            + [Topic.scala](../../../../main/scala/ohnosequences/awstools/sns/Topic.md)
          + sqs
            + [Queue.scala](../../../../main/scala/ohnosequences/awstools/sqs/Queue.md)
            + [SQS.scala](../../../../main/scala/ohnosequences/awstools/sqs/SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](DynamoDBTests.md)
          + [EC2Tests.scala](EC2Tests.md)
          + [S3Tests.scala](S3Tests.md)
          + [SNSTests.scala](SNSTests.md)
          + [SQSTests.scala](SQSTests.md)

------


```scala
package ohnosequences.awstools.sqs

import org.junit.Test
import org.junit.Assert._

import java.io.File

class SQSTests {

  @Test
  def policyTests {

//    val sqs = SQS.create(new File("AwsCredentials.properties"))
//    val queueName = "test_" + System.currentTimeMillis
//    val queue = sqs.createQueue(queueName)
//
//    try {
//      assertEquals(queue.url, sqs.getQueueByName(queueName).get.url)
//      assertEquals(None, sqs.getQueueByName("unexisting queue"))
//    } finally {
//      queue.delete()
//      sqs.shutdown()
//    }
  }
}


```

