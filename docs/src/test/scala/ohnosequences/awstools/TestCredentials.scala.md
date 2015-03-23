
```scala
package ohnosequences.awstools

object TestCredentials {
  val aws: Option[AWSClients] = {
    generated.test.credentials.credentialsProvider.map (AWSClients.create(_))
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

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\..\..\..\main\scala\ohnosequences\awstools\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\..\..\main\scala\ohnosequences\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\..\..\main\scala\ohnosequences\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\..\..\main\scala\ohnosequences\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: TestCredentials.scala.md