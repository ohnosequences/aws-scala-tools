
```scala
package ohnosequences.awstools.sns

import java.io.File

import ohnosequences.awstools.regions.Region._

import com.amazonaws.auth._
import com.amazonaws.services.sns.{AmazonSNSClient, AmazonSNS}
import com.amazonaws.services.sns.model.{CreateTopicRequest}
import com.amazonaws.internal.StaticCredentialsProvider

class SNS(val sns: AmazonSNS) {

  def createTopic(name: String) = {
    Topic(sns, sns.createTopic(new CreateTopicRequest(name)).getTopicArn, name)
  }

  def shutdown() {
    sns.shutdown()
  }

}

object SNS {

  def create(): SNS = {
    create(new InstanceProfileCredentialsProvider())
  }

  def create(credentialsFile: File): SNS = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)))
  }

  def create(accessKey: String, secretKey: String): SNS = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
  }

  def create(credentials: AWSCredentialsProvider, region: ohnosequences.awstools.regions.Region = Ireland): SNS = {
    val snsClient = new AmazonSNSClient(credentials)
    snsClient.setRegion(region)
    new SNS(snsClient)
  }
}




```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: ../dynamodb/DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: ../dynamodb/DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: ../dynamodb/Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: ../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: ../../../../../test/scala/ohnosequences/awstools/InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md