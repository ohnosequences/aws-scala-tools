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
            + [CloudWatch.scala](CloudWatch.md)
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
            + [Queue.scala](../sqs/Queue.md)
            + [SQS.scala](../sqs/SQS.md)
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
package ohnosequences.awstools.cloudwatch

import java.io.File

import com.amazonaws.auth._
import com.amazonaws.services.cloudwatch.{model, AmazonCloudWatchClient, AmazonCloudWatch}
import com.amazonaws.services.cloudwatch.model.{GetMetricStatisticsRequest, StandardUnit, MetricDatum, PutMetricDataRequest}
import com.amazonaws.regions.Regions
import com.amazonaws.internal.StaticCredentialsProvider

class CloudWatch(val cw: AmazonCloudWatch) {

  def shutdown() {
    cw.shutdown()
  }

  def putMetricData(metric: String, namespace: String, value: Double) {
    cw.putMetricData(
      new PutMetricDataRequest()
        .withNamespace(namespace)
        .withMetricData(
          new MetricDatum()
            .withMetricName(metric)
            .withUnit(StandardUnit.Count)
            .withValue(value)
        )
    )
  }

  def getStats(metric: String) = {
    cw.getMetricStatistics(
      new GetMetricStatisticsRequest()
        .withMetricName(metric)
    )
  }



}

object CloudWatch {

  def create(): CloudWatch = {
    create(new InstanceProfileCredentialsProvider())
  }


  def create(credentialsFile: File): CloudWatch = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)))
  }

  def create(accessKey: String, secretKey: String): CloudWatch = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
  }

  def create(credentials: AWSCredentialsProvider): CloudWatch = {
    val cwClient = new AmazonCloudWatchClient(credentials)
    cwClient.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    new CloudWatch(cwClient)
  }
}

```

