
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

sealed class InstanceType private(val name: String) {
  override def toString = name

  def toAWS = JavaInstanceType.fromValue(name)
}

object InstanceType {

  implicit def toJavaInstanceType(t: InstanceType): JavaInstanceType =
    JavaInstanceType.fromValue(t.name)

  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  def fromName(name: String): InstanceType = new InstanceType(name)

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html

  // TODO: write tests that check that the string name corresponds to the object name
  // Current Generation Instances //

  // General purpose
  case object t2 {
    case object micro  extends InstanceType("t2.micro")
    case object small  extends InstanceType("t2.small")
    case object medium extends InstanceType("t2.medium")
    case object large  extends InstanceType("t2.large")
  }
  case object m4 {
    case object large    extends InstanceType("m4.large")
    case object xlarge   extends InstanceType("m4.xlarge")
    case object x2large  extends InstanceType("m4.2xlarge")
    case object x4large  extends InstanceType("m4.4xlarge")
    case object x10large extends InstanceType("m4.10xlarge")
  }
  case object m3 {
    case object medium  extends InstanceType("m3.medium")
    case object large   extends InstanceType("m3.large")
    case object xlarge  extends InstanceType("m3.xlarge")
    case object x2large extends InstanceType("m3.2xlarge")
  }

  // Compute optimized
  case object c4 {
    case object large   extends InstanceType("c4.large")
    case object xlarge  extends InstanceType("c4.xlarge")
    case object x2large extends InstanceType("c4.2xlarge")
    case object x4large extends InstanceType("c4.4xlarge")
    case object x8large extends InstanceType("c4.8xlarge")
  }
  case object c3 {
    case object large   extends InstanceType("c3.large")
    case object xlarge  extends InstanceType("c3.xlarge")
    case object x2large extends InstanceType("c3.2xlarge")
    case object x4large extends InstanceType("c3.4xlarge")
    case object x8large extends InstanceType("c3.8xlarge")
  }

  // Memory optimized
  case object r3 {
    case object large   extends InstanceType("r3.large")
    case object xlarge  extends InstanceType("r3.xlarge")
    case object x2large extends InstanceType("r3.2xlarge")
    case object x4large extends InstanceType("r3.4xlarge")
    case object x8large extends InstanceType("r3.8xlarge")
  }

  // Storage optimized
  case object i2 {
    case object xlarge  extends InstanceType("i2.xlarge")
    case object x2large extends InstanceType("i2.2xlarge")
    case object x4large extends InstanceType("i2.4xlarge")
    case object x8large extends InstanceType("i2.8xlarge")
  }

  case object d2 {
    case object xlarge  extends InstanceType("d2.xlarge")
    case object x2large extends InstanceType("d2.2xlarge")
    case object x4large extends InstanceType("d2.4xlarge")
    case object x8large extends InstanceType("d2.8xlarge")
  }


  // Previous Generation Instances //

  // General purpose
  case object m1 {
    case object small  extends InstanceType("m1.small")
    case object medium extends InstanceType("m1.medium")
    case object large  extends InstanceType("m1.large")
    case object xlarge extends InstanceType("m1.xlarge")
  }

  // Compute optimized
  case object c1 {
    case object medium  extends InstanceType("c1.medium")
    case object xlarge  extends InstanceType("c1.xlarge")
  }
  case object cc2 {
    case object x8large extends InstanceType("cc2.8xlarge")
  }

  // Memory optimized
  case object m2 {
    case object xlarge  extends InstanceType("m2.xlarge")
    case object x2large extends InstanceType("m2.2xlarge")
    case object x4large extends InstanceType("m2.4xlarge")
  }
  case object cr1 {
    case object x8large extends InstanceType("cr1.8xlarge")
  }

  // Storage optimized
  case object hi1 {
    case object x4large extends InstanceType("hi1.4xlarge")
  }
  case object hs1 {
    case object x8large extends InstanceType("hs1.8xlarge")
  }

  // GPU instances
  case object cg1 {
    case object x4large extends InstanceType("cg1.4xlarge")
  }

  // Micro instances
  case object t1 {
    case object micro extends InstanceType("t1.micro")
  }

}

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md