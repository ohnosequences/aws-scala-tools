
```scala
package ohnosequences.awstools.ec2


trait AnyInstanceSpecs {

  type AMI <: AnyAMI
  val  ami: AMI

  type InstanceType <: AnyInstanceType
  val  instanceType: InstanceType

  // NOTE: we don't require it here, because in some place we create an instance of this type from a java-sdk type >_<
  // val supportsAMI: T SupportsAMI A
}

case class InstanceSpecs[
  A <: AnyLinuxAMI,
  T <: AnyInstanceType
](val ami: A,
  val instanceType: T
)(implicit
  val supportsAMI: T SupportsAMI A
) extends AnyInstanceSpecs {

  type AMI = A
  type InstanceType = T
}
```

An instance type supports an AMI if it supports both its storage type and virtualization

```scala
@annotation.implicitNotFound( msg = """
Instance type

  ${T}

doesn't support the AMI type

  ${A}

Try to choose different virtualization or storage type.
""")
sealed trait SupportsAMI[T <: AnyInstanceType, A <: AnyLinuxAMI]
case object SupportsAMI {

  implicit def supports[
    T <: AnyInstanceType,
    A <: AnyLinuxAMI
  ](implicit
    stor: T SupportsStorageType A#Storage,
    virt: T SupportsVirtualization A#Virt
  ):  (T SupportsAMI A) =
  new (T SupportsAMI A) {}
}


// TODO: check it with this table: http://aws.amazon.com/amazon-linux-ami/instance-type-matrix/

// The list can be retrieved from http://www.ec2instances.info/?min_storage=1
@annotation.implicitNotFound( msg = """
Instance type

  ${T}

doesn't support storage type of the chosen AMI

  ${S}
""")
sealed trait SupportsStorageType[T <: AnyInstanceType, S <: AnyStorageType]
case object SupportsStorageType {
  import InstanceType._

  implicit def ebs[T <: AnyInstanceType]:
      (T SupportsStorageType EBS.type) =
  new (T SupportsStorageType EBS.type) {}

  implicit def is_m3[T <: AnyInstanceType.ofFamily[m3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_r3[T <: AnyInstanceType.ofFamily[r3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_m1[T <: AnyInstanceType.ofFamily[m1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_m2[T <: AnyInstanceType.ofFamily[m2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_i2[T <: AnyInstanceType.ofFamily[i2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_d2[T <: AnyInstanceType.ofFamily[d2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cr1[T <: AnyInstanceType.ofFamily[cr1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cg1[T <: AnyInstanceType.ofFamily[cg1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cc2[T <: AnyInstanceType.ofFamily[cc2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_c3[T <: AnyInstanceType.ofFamily[c3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_c1[T <: AnyInstanceType.ofFamily[c1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  // TODO: what's g2 instances?
}


@annotation.implicitNotFound( msg = """
Instance type

  ${T}

doesn't support virtualization of the chosen AMI

  ${V}
""")
sealed trait SupportsVirtualization[T <: AnyInstanceType, V <: AnyVirtualization]
case object SupportsVirtualization {
  import InstanceType._
```

All current generation instance types support HVM AMIs.
The CC2, CR1, HI1, and HS1 previous generation instance types support HVM AMIs.

```scala
  implicit def hvm_CurrentGeneration[T <: AnyInstanceType.ofGeneration[CurrentGeneration]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_cc2[T <: AnyInstanceType.ofFamily[cc2.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_cr1[T <: AnyInstanceType.ofFamily[cr1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}
```

The C3 and M3 current generation instance types support PV AMIs.
The C1, HI1, HS1, M1, M2, and T1 previous generation instance types support PV AMIs.

```scala
  implicit def pv_c3[T <: AnyInstanceType.ofFamily[c3.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m3[T <: AnyInstanceType.ofFamily[m3.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_c1[T <: AnyInstanceType.ofFamily[c1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m1[T <: AnyInstanceType.ofFamily[m1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m2[T <: AnyInstanceType.ofFamily[m2.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_t1[T <: AnyInstanceType.ofFamily[t1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}
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