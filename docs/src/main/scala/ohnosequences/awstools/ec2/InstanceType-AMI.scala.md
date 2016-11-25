
```scala
package ohnosequences.awstools.ec2
```

## Instance Types & AMI compatibility implicits

Here we provide implicits for (hopefully) all valid combinations of

- instance type & storage type
- instance type & virtualization type
- instance type & AMI (as a combination of storage and virtualization types)

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