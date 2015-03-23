
```scala
package ohnosequences.awstools.ec2

import org.junit.Test
import org.junit.Assert._

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

import ohnosequences.awstools.ec2.InstanceType._

class InstanceTypeTests {

  @Test
  def toJavaTypeTest() {

    // General purpose
    assert{ t2_micro.toAWS   == JavaInstanceType.fromValue("t2.micro") }
    assert{ t2_small.toAWS   == JavaInstanceType.fromValue("t2.small") }
    assert{ t2_medium.toAWS  == JavaInstanceType.fromValue("t2.medium") }
    assert{ m3_medium.toAWS  == JavaInstanceType.fromValue("m3.medium") }
    assert{ m3_large.toAWS   == JavaInstanceType.fromValue("m3.large") }
    assert{ m3_xlarge.toAWS  == JavaInstanceType.fromValue("m3.xlarge") }
    assert{ m3_2xlarge.toAWS == JavaInstanceType.fromValue("m3.2xlarge") }
    // Compute optimized
    assert{ c3_large.toAWS   == JavaInstanceType.fromValue("c3.large") }
    assert{ c3_xlarge.toAWS  == JavaInstanceType.fromValue("c3.xlarge") }
    assert{ c3_2xlarge.toAWS == JavaInstanceType.fromValue("c3.2xlarge") }
    assert{ c3_4xlarge.toAWS == JavaInstanceType.fromValue("c3.4xlarge") }
    assert{ c3_8xlarge.toAWS == JavaInstanceType.fromValue("c3.8xlarge") }
    // Memory optimized
    assert{ r3_large.toAWS   == JavaInstanceType.fromValue("r3.large") }
    assert{ r3_xlarge.toAWS  == JavaInstanceType.fromValue("r3.xlarge") }
    assert{ r3_2xlarge.toAWS == JavaInstanceType.fromValue("r3.2xlarge") }
    assert{ r3_4xlarge.toAWS == JavaInstanceType.fromValue("r3.4xlarge") }
    assert{ r3_8xlarge.toAWS == JavaInstanceType.fromValue("r3.8xlarge") }
    // Storage optimized
    assert{ i2_xlarge.toAWS   == JavaInstanceType.fromValue("i2.xlarge") }
    assert{ i2_2xlarge.toAWS  == JavaInstanceType.fromValue("i2.2xlarge") }
    assert{ i2_4xlarge.toAWS  == JavaInstanceType.fromValue("i2.4xlarge") }
    assert{ i2_8xlarge.toAWS  == JavaInstanceType.fromValue("i2.8xlarge") }
    assert{ hs1_8xlarge.toAWS == JavaInstanceType.fromValue("hs1.8xlarge") }
    // GPU instances
    assert{ g2_2xlarge.toAWS == JavaInstanceType.fromValue("g2.2xlarge") }

    // Previous Generation Instances //

    // General purpose
    assert{ m1_small.toAWS  == JavaInstanceType.fromValue("m1.small") }
    assert{ m1_medium.toAWS == JavaInstanceType.fromValue("m1.medium") }
    assert{ m1_large.toAWS  == JavaInstanceType.fromValue("m1.large") }
    assert{ m1_xlarge.toAWS == JavaInstanceType.fromValue("m1.xlarge") }
    // Compute optimized
    assert{ c1_medium.toAWS   == JavaInstanceType.fromValue("c1.medium") }
    assert{ c1_xlarge.toAWS   == JavaInstanceType.fromValue("c1.xlarge") }
    assert{ cc2_8xlarge.toAWS == JavaInstanceType.fromValue("cc2.8xlarge") }
    // Memory optimized
    assert{ m2_xlarge.toAWS   == JavaInstanceType.fromValue("m2.xlarge") }
    assert{ m2_2xlarge.toAWS  == JavaInstanceType.fromValue("m2.2xlarge") }
    assert{ m2_4xlarge.toAWS  == JavaInstanceType.fromValue("m2.4xlarge") }
    assert{ cr1_8xlarge.toAWS == JavaInstanceType.fromValue("cr1.8xlarge") }
    // Storage optimized
    assert{ hi1_4xlarge.toAWS == JavaInstanceType.fromValue("hi1.4xlarge") }
    // GPU instances
    assert{ cg1_4xlarge.toAWS == JavaInstanceType.fromValue("cg1.4xlarge") }
    // Micro instances
    assert{ t1_micro.toAWS == JavaInstanceType.fromValue("t1.micro") }

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