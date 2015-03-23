
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

sealed class InstanceType private(val name: String) {
  override def toString = name

  //@deprecated("There is an implicit conversion for that in ohnosequences.awstools.ec2.InstanceType, just import it",
  //            since = "v0.6.0")
  def toAWS = JavaInstanceType.fromValue(name)
}

object InstanceType {

  implicit def toJavaInstanceType(t: InstanceType): JavaInstanceType = 
    JavaInstanceType.fromValue(t.name)

  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  def fromName(name: String): InstanceType = new InstanceType(name)

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html

  // Current Generation Instances //

  // General purpose
  case object t2_micro   extends InstanceType("t2.micro")
  case object t2_small   extends InstanceType("t2.small")
  case object t2_medium  extends InstanceType("t2.medium")
  case object m3_medium  extends InstanceType("m3.medium")
  case object m3_large   extends InstanceType("m3.large")
  case object m3_xlarge  extends InstanceType("m3.xlarge")
  case object m3_2xlarge extends InstanceType("m3.2xlarge")
  // Compute optimized
  case object c3_large   extends InstanceType("c3.large")
  case object c3_xlarge  extends InstanceType("c3.xlarge")
  case object c3_2xlarge extends InstanceType("c3.2xlarge")
  case object c3_4xlarge extends InstanceType("c3.4xlarge")
  case object c3_8xlarge extends InstanceType("c3.8xlarge")
  // Memory optimized
  case object r3_large   extends InstanceType("r3.large")
  case object r3_xlarge  extends InstanceType("r3.xlarge")
  case object r3_2xlarge extends InstanceType("r3.2xlarge")
  case object r3_4xlarge extends InstanceType("r3.4xlarge")
  case object r3_8xlarge extends InstanceType("r3.8xlarge")
  // Storage optimized
  case object i2_xlarge   extends InstanceType("i2.xlarge")
  case object i2_2xlarge  extends InstanceType("i2.2xlarge")
  case object i2_4xlarge  extends InstanceType("i2.4xlarge")
  case object i2_8xlarge  extends InstanceType("i2.8xlarge")
  case object hs1_8xlarge extends InstanceType("hs1.8xlarge")
  // GPU instances
  case object g2_2xlarge extends InstanceType("g2.2xlarge")

  // Previous Generation Instances //

  // General purpose
  case object m1_small  extends InstanceType("m1.small")
  case object m1_medium extends InstanceType("m1.medium")
  case object m1_large  extends InstanceType("m1.large")
  case object m1_xlarge extends InstanceType("m1.xlarge")
  // Compute optimized
  case object c1_medium   extends InstanceType("c1.medium")
  case object c1_xlarge   extends InstanceType("c1.xlarge")
  case object cc2_8xlarge extends InstanceType("cc2.8xlarge")
  // Memory optimized
  case object m2_xlarge   extends InstanceType("m2.xlarge")
  case object m2_2xlarge  extends InstanceType("m2.2xlarge")
  case object m2_4xlarge  extends InstanceType("m2.4xlarge")
  case object cr1_8xlarge extends InstanceType("cr1.8xlarge")
  // Storage optimized
  case object hi1_4xlarge extends InstanceType("hi1.4xlarge")
  // GPU instances
  case object cg1_4xlarge extends InstanceType("cg1.4xlarge")
  // Micro instances
  case object t1_micro extends InstanceType("t1.micro")

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

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md