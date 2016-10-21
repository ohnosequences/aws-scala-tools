package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions._

/* ## [Amazon Machine Images (AMI)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html) */


sealed abstract class Architecture(val wordSize: Int)
case object x86_32 extends Architecture(32)
case object x86_64 extends Architecture(64)


/* ### [Linux AMI Virtualization Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/virtualization_types.html) */
sealed trait AnyVirtualization
/* All current generation instance types support HVM AMIs.
   The CC2, CR1, HI1, and HS1 previous generation instance types support HVM AMIs. */
case object HVM extends AnyVirtualization //; type HVM = HVM.type
/* The C3 and M3 current generation instance types support PV AMIs.
   The C1, HI1, HS1, M1, M2, and T1 previous generation instance types support PV AMIs. */
case object PV extends AnyVirtualization //; type PV = PV.type


/* ### [Storage for the Root Device](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#storage-for-the-root-device) */
sealed trait AnyStorageType
case object EBS extends AnyStorageType //; type EBS = EBS.type
case object InstanceStore extends AnyStorageType //; type InstanceStore = InstanceStore.type


// TODO: [Launch permissions](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#launch-permissions)


/* Just some base trait: */
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
trait AnyAmazonLinuxAMI {

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


  implicit case object ami_c481fad3 extends AmazonLinuxAMI(NorthernVirginia, HVM, EBS)
  implicit case object ami_4487fc53 extends AmazonLinuxAMI(NorthernVirginia, HVM, InstanceStore)
  implicit case object ami_4d87fc5a extends AmazonLinuxAMI(NorthernVirginia, PV,  EBS)
  implicit case object ami_4287fc55 extends AmazonLinuxAMI(NorthernVirginia, PV,  InstanceStore)

  // TODO: add Ohio (PV is N/A)

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

  // TODO: add Seoul (PV is N/A)

  implicit case object ami_1a15c77b extends AmazonLinuxAMI(Tokyo, HVM, EBS)
  implicit case object ami_9016c4f1 extends AmazonLinuxAMI(Tokyo, HVM, InstanceStore)
  implicit case object ami_cf14c6ae extends AmazonLinuxAMI(Tokyo, PV,  EBS)
  implicit case object ami_4615c727 extends AmazonLinuxAMI(Tokyo, PV,  InstanceStore)

  implicit case object ami_55d4e436 extends AmazonLinuxAMI(Sydney, HVM, EBS)
  implicit case object ami_fbd6e698 extends AmazonLinuxAMI(Sydney, HVM, InstanceStore)
  implicit case object ami_3ad6e659 extends AmazonLinuxAMI(Sydney, PV,  EBS)
  implicit case object ami_3fd6e65c extends AmazonLinuxAMI(Sydney, PV,  InstanceStore)

  // TODO: add Mumbai (PV is N/A)

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
