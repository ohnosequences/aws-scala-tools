
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.{ model => amzn }

sealed trait AnyInstanceType {
  type Family <: InstanceType.Family
  val  family: Family

  val size: String

  final lazy val name: String = s"${family.prefix}.${size}"
  override def toString = name

  def toAWS = amzn.InstanceType.fromValue(name)
}

case object AnyInstanceType {
  import InstanceType._

  type ofGeneration[G <: AnyGeneration] = AnyInstanceType { type Family <: G }
  type ofFamily[F <: Family] = AnyInstanceType { type Family = F }
}

sealed class InstanceType[
  F <: InstanceType.Family
](val family: F, val size: String) extends AnyInstanceType {
  type Family = F
}

case object InstanceType {

  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  private[awstools]
    def fromName(name: String): AnyInstanceType = {
       // TODO: improve the pattern
       val pattern = """(.\d)\.(.*)""".r
       name match {
        case pattern(prefix, size) => new InstanceType(new Family(prefix), size)
        case _ => throw new IllegalArgumentException(s"Couldn't parse instance type from [${name}]")
      }
    }

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html
  // TODO: write tests that check that the string name corresponds to the object name

  sealed trait AnyGeneration
  trait CurrentGeneration extends AnyGeneration
  trait PreviousGeneration extends AnyGeneration

  sealed class Family(val prefix: String)

  // Current Generation Instances //

  // General purpose
  case object t2 extends Family("t2") with CurrentGeneration {
    case object micro  extends InstanceType(t2, "micro")
    case object small  extends InstanceType(t2, "small")
    case object medium extends InstanceType(t2, "medium")
    case object large  extends InstanceType(t2, "large")
  }
  case object m4 extends Family("m4") with CurrentGeneration {
    case object large    extends InstanceType(m4, "large")
    case object xlarge   extends InstanceType(m4, "xlarge")
    case object x2large  extends InstanceType(m4, "2xlarge")
    case object x4large  extends InstanceType(m4, "4xlarge")
    case object x10large extends InstanceType(m4, "10xlarge")
  }
  case object m3 extends Family("m3") with CurrentGeneration {
    case object medium  extends InstanceType(m3, "medium")
    case object large   extends InstanceType(m3, "large")
    case object xlarge  extends InstanceType(m3, "xlarge")
    case object x2large extends InstanceType(m3, "2xlarge")
  }

  // Compute optimized
  case object c4 extends Family("c4") with CurrentGeneration {
    case object large   extends InstanceType(c4, "large")
    case object xlarge  extends InstanceType(c4, "xlarge")
    case object x2large extends InstanceType(c4, "2xlarge")
    case object x4large extends InstanceType(c4, "4xlarge")
    case object x8large extends InstanceType(c4, "8xlarge")
  }
  case object c3 extends Family("c3") with CurrentGeneration {
    case object large   extends InstanceType(c3, "large")
    case object xlarge  extends InstanceType(c3, "xlarge")
    case object x2large extends InstanceType(c3, "2xlarge")
    case object x4large extends InstanceType(c3, "4xlarge")
    case object x8large extends InstanceType(c3, "8xlarge")
  }

  // Memory optimized
  case object r3 extends Family("r3") with CurrentGeneration {
    case object large   extends InstanceType(r3, "large")
    case object xlarge  extends InstanceType(r3, "xlarge")
    case object x2large extends InstanceType(r3, "2xlarge")
    case object x4large extends InstanceType(r3, "4xlarge")
    case object x8large extends InstanceType(r3, "8xlarge")
  }

  // Storage optimized
  case object i2 extends Family("i2") with CurrentGeneration {
    case object xlarge  extends InstanceType(i2, "xlarge")
    case object x2large extends InstanceType(i2, "2xlarge")
    case object x4large extends InstanceType(i2, "4xlarge")
    case object x8large extends InstanceType(i2, "8xlarge")
  }

  case object d2 extends Family("d2") with CurrentGeneration {
    case object xlarge  extends InstanceType(d2, "xlarge")
    case object x2large extends InstanceType(d2, "2xlarge")
    case object x4large extends InstanceType(d2, "4xlarge")
    case object x8large extends InstanceType(d2, "8xlarge")
  }


  // Previous Generation Instances //

  // General purpose
  case object m1 extends Family("m1") with PreviousGeneration {
    case object small  extends InstanceType(m1, "small")
    case object medium extends InstanceType(m1, "medium")
    case object large  extends InstanceType(m1, "large")
    case object xlarge extends InstanceType(m1, "xlarge")
  }

  // Compute optimized
  case object c1 extends Family("c1") with PreviousGeneration {
    case object medium  extends InstanceType(c1, "medium")
    case object xlarge  extends InstanceType(c1, "xlarge")
  }
  case object cc2 extends Family("cc2") with PreviousGeneration {
    case object x8large extends InstanceType(cc2, "8xlarge")
  }

  // Memory optimized
  case object m2 extends Family("m2") with PreviousGeneration {
    case object xlarge  extends InstanceType(m2, "xlarge")
    case object x2large extends InstanceType(m2, "2xlarge")
    case object x4large extends InstanceType(m2, "4xlarge")
  }
  case object cr1 extends Family("cr1") with PreviousGeneration {
    case object x8large extends InstanceType(cr1, "8xlarge")
  }

  // Storage optimized
  case object hi1 extends Family("hi1") with PreviousGeneration {
    case object x4large extends InstanceType(hi1, "4xlarge")
  }
  case object hs1 extends Family("hs1") with PreviousGeneration {
    case object x8large extends InstanceType(hs1, "8xlarge")
  }

  // GPU instances
  case object cg1 extends Family("cg1") with PreviousGeneration {
    case object x4large extends InstanceType(cg1, "4xlarge")
  }

  // Micro instances
  case object t1 extends Family("t1") with PreviousGeneration {
    case object micro extends InstanceType(t1, "micro")
  }

}

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: package.scala.md
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