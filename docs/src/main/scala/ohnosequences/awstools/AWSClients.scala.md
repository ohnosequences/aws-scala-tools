
```scala
package ohnosequences.awstools

import com.amazonaws.auth.{AWSCredentialsProvider}
import ohnosequences.awstools.ec2.EC2
import ohnosequences.awstools.autoscaling.AutoScaling
import ohnosequences.awstools.sqs.SQS
import ohnosequences.awstools.sns.SNS
import ohnosequences.awstools.s3.S3
import ohnosequences.awstools.regions.Region.Ireland
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient

trait AWSClients {
  val ec2: EC2
  val as: AutoScaling
  val sqs: SQS
  val sns: SNS
  val s3: S3
  val ddb: AmazonDynamoDBClient
}

object AWSClients {
  def create(credentialsProvider: AWSCredentialsProvider, region: ohnosequences.awstools.regions.Region = Ireland) = new AWSClients {
    val ec2 = EC2.create(credentialsProvider, region)
    val as = AutoScaling.create(credentialsProvider, ec2, region)
    val sqs = SQS.create(credentialsProvider, region)
    val sns = SNS.create(credentialsProvider, region)
    val s3 = S3.create(credentialsProvider, region)
    val ddb = new AmazonDynamoDBClient(credentialsProvider)
    ddb.setRegion(region.toAWSRegion)
  }
}

```




[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../logging/S3Logger.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ec2/package.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: regions/Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: utils/SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: dynamodb/DynamoDBUtils.scala.md