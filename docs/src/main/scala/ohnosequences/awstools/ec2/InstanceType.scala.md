
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model

sealed trait AnyInstanceType {
  // NOTE: it forced to be a case object
  self: Singleton with Product =>

  type Family <: InstanceTypeFamily
  val  family: Family

  final lazy val size: String = self.productPrefix
  final lazy val name: String = s"${family}.${size}"

  override def toString: String = name
}

case object AnyInstanceType {

  type ofGeneration[G <: AnyGeneration]  = AnyInstanceType { type Family <: G }
  type ofFamily[F <: InstanceTypeFamily] = AnyInstanceType { type Family = F }

  implicit def toJavaInstanceType(t: AnyInstanceType):
    model.InstanceType =
    model.InstanceType.fromValue(t.name)
}

sealed class InstanceType[
  F <: InstanceTypeFamily
](val family: F) extends AnyInstanceType {
  // NOTE: it forced to be a case object
  self: Singleton with Product =>

  type Family = F
}


sealed trait AnyGeneration
trait CurrentGeneration extends AnyGeneration
trait PreviousGeneration extends AnyGeneration

sealed class InstanceTypeFamily {
  // NOTE: it forced to be a case object
  self: AnyGeneration with Singleton with Product =>
}
```

### Current Generation Instances

```scala
// This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html

```

#### General purpose

```scala
case object t2 extends InstanceTypeFamily with CurrentGeneration {
  case object nano   extends InstanceType(t2)
  case object micro  extends InstanceType(t2)
  case object small  extends InstanceType(t2)
  case object medium extends InstanceType(t2)
  case object large  extends InstanceType(t2)
}
case object m4 extends InstanceTypeFamily with CurrentGeneration {
  case object     large  extends InstanceType(m4)
  case object    xlarge  extends InstanceType(m4)
  case object  `2xlarge` extends InstanceType(m4)
  case object  `4xlarge` extends InstanceType(m4)
  case object `10xlarge` extends InstanceType(m4)
  case object `16xlarge` extends InstanceType(m4)
}
case object m3 extends InstanceTypeFamily with CurrentGeneration {
  case object medium    extends InstanceType(m3)
  case object    large  extends InstanceType(m3)
  case object   xlarge  extends InstanceType(m3)
  case object `2xlarge` extends InstanceType(m3)
}
```

#### Compute optimized

```scala
case object c4 extends InstanceTypeFamily with CurrentGeneration {
  case object    large  extends InstanceType(c4)
  case object   xlarge  extends InstanceType(c4)
  case object `2xlarge` extends InstanceType(c4)
  case object `4xlarge` extends InstanceType(c4)
  case object `8xlarge` extends InstanceType(c4)
}
case object c3 extends InstanceTypeFamily with CurrentGeneration {
  case object    large  extends InstanceType(c3)
  case object   xlarge  extends InstanceType(c3)
  case object `2xlarge` extends InstanceType(c3)
  case object `4xlarge` extends InstanceType(c3)
  case object `8xlarge` extends InstanceType(c3)
}
```

#### Memory optimized

```scala
case object r3 extends InstanceTypeFamily with CurrentGeneration {
  case object    large  extends InstanceType(r3)
  case object   xlarge  extends InstanceType(r3)
  case object `2xlarge` extends InstanceType(r3)
  case object `4xlarge` extends InstanceType(r3)
  case object `8xlarge` extends InstanceType(r3)
}
case object x1 extends InstanceTypeFamily with CurrentGeneration {
  case object `16xlarge` extends InstanceType(x1)
  case object `32xlarge` extends InstanceType(x1)
}
```

#### Storage optimized

```scala
case object i2 extends InstanceTypeFamily with CurrentGeneration {
  case object   xlarge  extends InstanceType(i2)
  case object `2xlarge` extends InstanceType(i2)
  case object `4xlarge` extends InstanceType(i2)
  case object `8xlarge` extends InstanceType(i2)
}
case object d2 extends InstanceTypeFamily with CurrentGeneration {
  case object   xlarge  extends InstanceType(d2)
  case object `2xlarge` extends InstanceType(d2)
  case object `4xlarge` extends InstanceType(d2)
  case object `8xlarge` extends InstanceType(d2)
}
```

#### Accelerated computing

```scala
case object p2 extends InstanceTypeFamily with CurrentGeneration {
  case object    xlarge  extends InstanceType(p2)
  case object  `8xlarge` extends InstanceType(p2)
  case object `16xlarge` extends InstanceType(p2)
}
case object g2 extends InstanceTypeFamily with CurrentGeneration {
  case object `2xlarge` extends InstanceType(g2)
  case object `8xlarge` extends InstanceType(g2)
}
```

### Previous Generation Instances
#### General purpose

```scala
case object m1 extends InstanceTypeFamily with PreviousGeneration {
  case object small  extends InstanceType(m1)
  case object medium extends InstanceType(m1)
  case object  large extends InstanceType(m1)
  case object xlarge extends InstanceType(m1)
}
```

#### Compute optimized

```scala
case object c1 extends InstanceTypeFamily with PreviousGeneration {
  case object medium extends InstanceType(c1)
  case object xlarge extends InstanceType(c1)
}
case object cc2 extends InstanceTypeFamily with PreviousGeneration {
  case object `8xlarge` extends InstanceType(cc2)
}
```

#### Memory optimized

```scala
case object m2 extends InstanceTypeFamily with PreviousGeneration {
  case object   xlarge  extends InstanceType(m2)
  case object `2xlarge` extends InstanceType(m2)
  case object `4xlarge` extends InstanceType(m2)
}
case object cr1 extends InstanceTypeFamily with PreviousGeneration {
  case object `8xlarge` extends InstanceType(cr1)
}
```

#### Storage optimized

```scala
case object hi1 extends InstanceTypeFamily with PreviousGeneration {
  case object `4xlarge` extends InstanceType(hi1)
}
case object hs1 extends InstanceTypeFamily with PreviousGeneration {
  case object `8xlarge` extends InstanceType(hs1)
}
```

#### GPU instances

```scala
case object cg1 extends InstanceTypeFamily with PreviousGeneration {
  case object `4xlarge` extends InstanceType(cg1)
}
```

#### Micro instances

```scala
case object t1 extends InstanceTypeFamily with PreviousGeneration {
  case object micro extends InstanceType(t1)
}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md