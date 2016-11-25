
```scala
package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions._
```

## [Amazon Machine Images (AMI)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html)

```scala
// TODO: com.amazonaws.services.ec2.model.ArchitectureValues
sealed abstract class Architecture(val wordSize: Int)
case object x86_32 extends Architecture(32)
case object x86_64 extends Architecture(64)
```

### [Linux AMI Virtualization Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/virtualization_types.html)

```scala
// TODO: com.amazonaws.services.ec2.model.VirtualizationType
sealed trait AnyVirtualization
```

All current generation instance types support HVM AMIs.
The CC2, CR1, HI1, and HS1 previous generation instance types support HVM AMIs.

```scala
case object HVM extends AnyVirtualization //; type HVM = HVM.type

```

The C3 and M3 current generation instance types support PV AMIs.
The C1, HI1, HS1, M1, M2, and T1 previous generation instance types support PV AMIs.

```scala
case object PV extends AnyVirtualization //; type PV = PV.type

```

### [Storage for the Root Device](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#storage-for-the-root-device)

```scala
// TODO: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/DeviceType.html
sealed trait AnyStorageType
case object EBS extends AnyStorageType //; type EBS = EBS.type
case object InstanceStore extends AnyStorageType //; type InstanceStore = InstanceStore.type


// TODO: [Launch permissions](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#launch-permissions)

```

Just some base trait:

```scala
trait AnyAMI { val id: String }

case class AMI(id: String) extends AnyAMI


trait AnyLinuxAMI extends AnyAMI {

  val version: String

  type Region <: RegionAlias
  val  region: Region

  type Arch <: Architecture
  val  arch: Arch

  type Virt <: AnyVirtualization
  val  virt: Virt

  type Storage <: AnyStorageType
  val  storage: Storage
}

// Amazon Linux AMI 2016.09.0
// See http://aws.amazon.com/amazon-linux-ami/
trait AnyAmazonLinuxAMI extends AnyLinuxAMI {

  final val version: String = "2016.09.0"

  type Arch = x86_64.type
  final val arch: Arch = x86_64
}

class AmazonLinuxAMI[
  R <: RegionAlias,
  V <: AnyVirtualization,
  S <: AnyStorageType
] private(
  val region: R,
  val virt: V,
  val storage: S
) extends AnyAmazonLinuxAMI {

  type Region = R
  type Virt = V
  type Storage = S

  val id = this.toString.replaceAll("_", "-")
}

case object AmazonLinuxAMI {
```

This constructor allows us to use refer to these AMIs through their parameters instead of explicit IDs. For example, you can write:

    ```scala
    AmazonLinuxAMI(Oregon, PV,  EBS)
    ```

    and you get `ami_1d49957d` with it's precise type and the corresponding ID.

    On the other hand you can't write `AmazonLinuxAMI(Ohio, PV, EBS)`, because such AMI doesn't exist.


```scala
  def apply[
    R <: RegionAlias,
    V <: AnyVirtualization,
    S <: AnyStorageType
  ](region: R,
    virt: V,
    storage: S
  )(implicit
    ami: AmazonLinuxAMI[R, V, S]
  ): ami.type = ami

  // NOTE: this list doesn't include HVM NAT

  implicit case object ami_c481fad3 extends AmazonLinuxAMI(NorthernVirginia, HVM, EBS)
  implicit case object ami_4487fc53 extends AmazonLinuxAMI(NorthernVirginia, HVM, InstanceStore)
  implicit case object ami_4d87fc5a extends AmazonLinuxAMI(NorthernVirginia, PV,  EBS)
  implicit case object ami_4287fc55 extends AmazonLinuxAMI(NorthernVirginia, PV,  InstanceStore)

  implicit case object ami_71ca9114 extends AmazonLinuxAMI(Ohio, HVM, EBS)
  implicit case object ami_70ca9115 extends AmazonLinuxAMI(Ohio, HVM, InstanceStore)

  implicit case object ami_b04e92d0 extends AmazonLinuxAMI(Oregon, HVM, EBS)
  implicit case object ami_dd4894bd extends AmazonLinuxAMI(Oregon, HVM, InstanceStore)
  implicit case object ami_1d49957d extends AmazonLinuxAMI(Oregon, PV,  EBS)
  implicit case object ami_48499528 extends AmazonLinuxAMI(Oregon, PV,  InstanceStore)

  implicit case object ami_de347abe extends AmazonLinuxAMI(NorthernCalifornia, HVM, EBS)
  implicit case object ami_9e3779fe extends AmazonLinuxAMI(NorthernCalifornia, HVM, InstanceStore)
  implicit case object ami_df3779bf extends AmazonLinuxAMI(NorthernCalifornia, PV,  EBS)
  implicit case object ami_69367809 extends AmazonLinuxAMI(NorthernCalifornia, PV,  InstanceStore)

  implicit case object ami_d41d58a7 extends AmazonLinuxAMI(Ireland, HVM, EBS)
  implicit case object ami_64105517 extends AmazonLinuxAMI(Ireland, HVM, InstanceStore)
  implicit case object ami_0e10557d extends AmazonLinuxAMI(Ireland, PV,  EBS)
  implicit case object ami_8c1d58ff extends AmazonLinuxAMI(Ireland, PV,  InstanceStore)

  implicit case object ami_0044b96f extends AmazonLinuxAMI(Frankfurt, HVM, EBS)
  implicit case object ami_a74ab7c8 extends AmazonLinuxAMI(Frankfurt, HVM, InstanceStore)
  implicit case object ami_1345b87c extends AmazonLinuxAMI(Frankfurt, PV,  EBS)
  implicit case object ami_f64ab799 extends AmazonLinuxAMI(Frankfurt, PV,  InstanceStore)

  implicit case object ami_7243e611 extends AmazonLinuxAMI(Singapore, HVM, EBS)
  implicit case object ami_4841e42b extends AmazonLinuxAMI(Singapore, HVM, InstanceStore)
  implicit case object ami_a743e6c4 extends AmazonLinuxAMI(Singapore, PV,  EBS)
  implicit case object ami_d846e3bb extends AmazonLinuxAMI(Singapore, PV,  InstanceStore)

  implicit case object ami_a04297ce extends AmazonLinuxAMI(Seoul, HVM, EBS)
  implicit case object ami_d34c99bd extends AmazonLinuxAMI(Seoul, HVM, InstanceStore)

  implicit case object ami_1a15c77b extends AmazonLinuxAMI(Tokyo, HVM, EBS)
  implicit case object ami_9016c4f1 extends AmazonLinuxAMI(Tokyo, HVM, InstanceStore)
  implicit case object ami_cf14c6ae extends AmazonLinuxAMI(Tokyo, PV,  EBS)
  implicit case object ami_4615c727 extends AmazonLinuxAMI(Tokyo, PV,  InstanceStore)

  implicit case object ami_55d4e436 extends AmazonLinuxAMI(Sydney, HVM, EBS)
  implicit case object ami_fbd6e698 extends AmazonLinuxAMI(Sydney, HVM, InstanceStore)
  implicit case object ami_3ad6e659 extends AmazonLinuxAMI(Sydney, PV,  EBS)
  implicit case object ami_3fd6e65c extends AmazonLinuxAMI(Sydney, PV,  InstanceStore)

  implicit case object ami_cacbbea5 extends AmazonLinuxAMI(Mumbai, HVM, EBS)
  implicit case object ami_cec2b7a1 extends AmazonLinuxAMI(Mumbai, HVM, InstanceStore)

  implicit case object ami_b777e4db extends AmazonLinuxAMI(SaoPaulo, HVM, EBS)
  implicit case object ami_5075e63c extends AmazonLinuxAMI(SaoPaulo, HVM, InstanceStore)
  implicit case object ami_1d75e671 extends AmazonLinuxAMI(SaoPaulo, PV,  EBS)
  implicit case object ami_b477e4d8 extends AmazonLinuxAMI(SaoPaulo, PV,  InstanceStore)

  implicit case object ami_fa875397 extends AmazonLinuxAMI(Beijing, HVM, EBS)
  implicit case object ami_cb8357a6 extends AmazonLinuxAMI(Beijing, HVM, InstanceStore)
  implicit case object ami_d98357b4 extends AmazonLinuxAMI(Beijing, PV,  EBS)
  implicit case object ami_1a8e5a77 extends AmazonLinuxAMI(Beijing, PV,  InstanceStore)

  implicit case object ami_7b4df41a extends AmazonLinuxAMI(GovCloud, HVM, EBS)
  implicit case object ami_ae4bf2cf extends AmazonLinuxAMI(GovCloud, HVM, InstanceStore)
  implicit case object ami_144cf575 extends AmazonLinuxAMI(GovCloud, PV,  EBS)
  implicit case object ami_bc48f1dd extends AmazonLinuxAMI(GovCloud, PV,  InstanceStore)

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