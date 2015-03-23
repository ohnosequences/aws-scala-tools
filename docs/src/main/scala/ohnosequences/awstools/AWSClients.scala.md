
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


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]
            + [AutoScalingGroup.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]
          + [AWSClients.scala][main\scala\ohnosequences\awstools\AWSClients.scala]
          + dynamodb
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]
          + ec2
            + [EC2.scala][main\scala\ohnosequences\awstools\ec2\EC2.scala]
            + [Filters.scala][main\scala\ohnosequences\awstools\ec2\Filters.scala]
            + [InstanceType.scala][main\scala\ohnosequences\awstools\ec2\InstanceType.scala]
            + [Utils.scala][main\scala\ohnosequences\awstools\ec2\Utils.scala]
          + regions
            + [Region.scala][main\scala\ohnosequences\awstools\regions\Region.scala]
          + s3
            + [Bucket.scala][main\scala\ohnosequences\awstools\s3\Bucket.scala]
            + [S3.scala][main\scala\ohnosequences\awstools\s3\S3.scala]
          + sns
            + [SNS.scala][main\scala\ohnosequences\awstools\sns\SNS.scala]
            + [Topic.scala][main\scala\ohnosequences\awstools\sns\Topic.scala]
          + sqs
            + [Queue.scala][main\scala\ohnosequences\awstools\sqs\Queue.scala]
            + [SQS.scala][main\scala\ohnosequences\awstools\sqs\SQS.scala]
          + utils
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]
            + [SQSUtils.scala][main\scala\ohnosequences\awstools\utils\SQSUtils.scala]
        + benchmark
          + [Benchmark.scala][main\scala\ohnosequences\benchmark\Benchmark.scala]
        + logging
          + [Logger.scala][main\scala\ohnosequences\logging\Logger.scala]
          + [S3Logger.scala][main\scala\ohnosequences\logging\S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [EC2Tests.scala][test\scala\ohnosequences\awstools\EC2Tests.scala]
          + [InstanceTypeTests.scala][test\scala\ohnosequences\awstools\InstanceTypeTests.scala]
          + [RegionTests.scala][test\scala\ohnosequences\awstools\RegionTests.scala]
          + [S3Tests.scala][test\scala\ohnosequences\awstools\S3Tests.scala]
          + [SQSTests.scala][test\scala\ohnosequences\awstools\SQSTests.scala]
          + [TestCredentials.scala][test\scala\ohnosequences\awstools\TestCredentials.scala]

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md