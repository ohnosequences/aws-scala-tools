package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions._

/* ## [Amazon Machine Images (AMI)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html) */


// TODO: com.amazonaws.services.ec2.model.ArchitectureValues
sealed abstract class Architecture(val wordSize: Int)
case object x86_32 extends Architecture(32)
case object x86_64 extends Architecture(64)


/* ### [Linux AMI Virtualization Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/virtualization_types.html) */
// TODO: com.amazonaws.services.ec2.model.VirtualizationType
sealed trait AnyVirtualization
/* All current generation instance types support HVM AMIs.
   The CC2, CR1, HI1, and HS1 previous generation instance types support HVM AMIs. */
case object HVM extends AnyVirtualization //; type HVM = HVM.type
/* The C3 and M3 current generation instance types support PV AMIs.
   The C1, HI1, HS1, M1, M2, and T1 previous generation instance types support PV AMIs. */
case object PV extends AnyVirtualization //; type PV = PV.type


/* ### [Storage for the Root Device](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#storage-for-the-root-device) */
// TODO: http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/DeviceType.html
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

// Amazon Linux AMI 2017.09.0
// See http://aws.amazon.com/amazon-linux-ami/
trait AnyAmazonLinuxAMI extends AnyLinuxAMI {

  final val version: String = "2017.09.0"

  type Arch = x86_64.type
  final val arch: Arch = x86_64
}

sealed class AmazonLinuxAMI[
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

  /* This constructor allows us to use refer to these AMIs through their parameters instead of explicit IDs. For example, you can write:

    ```scala
    AmazonLinuxAMI(Oregon, PV,  EBS)
    ```

    and you get `ami_1d49957d` with it's precise type and the corresponding ID.

    On the other hand you can't write `AmazonLinuxAMI(Ohio, PV, EBS)`, because such AMI doesn't exist.
  */
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

  implicit case object ami_8c1be5f6 extends AmazonLinuxAMI(NorthernVirginia, HVM, EBS)
  implicit case object ami_a518e6df extends AmazonLinuxAMI(NorthernVirginia, HVM, InstanceStore)
  implicit case object ami_fe16e884 extends AmazonLinuxAMI(NorthernVirginia, PV,  EBS)
  implicit case object ami_ec19e796 extends AmazonLinuxAMI(NorthernVirginia, PV,  InstanceStore)

  implicit case object ami_c5062ba0 extends AmazonLinuxAMI(Ohio, HVM, EBS)
  implicit case object ami_6b012c0e extends AmazonLinuxAMI(Ohio, HVM, InstanceStore)

  implicit case object ami_e689729e extends AmazonLinuxAMI(Oregon, HVM, EBS)
  implicit case object ami_01897279 extends AmazonLinuxAMI(Oregon, HVM, InstanceStore)
  implicit case object ami_e389729b extends AmazonLinuxAMI(Oregon, PV,  EBS)
  implicit case object ami_d8906ba0 extends AmazonLinuxAMI(Oregon, PV,  InstanceStore)

  implicit case object ami_02eada62 extends AmazonLinuxAMI(NorthernCalifornia, HVM, EBS)
  implicit case object ami_d7eadab7 extends AmazonLinuxAMI(NorthernCalifornia, HVM, InstanceStore)
  implicit case object ami_fce9d99c extends AmazonLinuxAMI(NorthernCalifornia, PV,  EBS)
  implicit case object ami_d1eadab1 extends AmazonLinuxAMI(NorthernCalifornia, PV,  InstanceStore)

  implicit case object ami_fd55ec99 extends AmazonLinuxAMI(CanadaCentral, HVM, EBS)
  implicit case object ami_be54edda extends AmazonLinuxAMI(CanadaCentral, HVM, InstanceStore)

  implicit case object ami_acd005d5 extends AmazonLinuxAMI(Ireland, HVM, EBS)
  implicit case object ami_aed005d7 extends AmazonLinuxAMI(Ireland, HVM, InstanceStore)
  implicit case object ami_07df0a7e extends AmazonLinuxAMI(Ireland, PV,  EBS)
  implicit case object ami_acd207d5 extends AmazonLinuxAMI(Ireland, PV,  InstanceStore)

  implicit case object ami_1a7f6d7e extends AmazonLinuxAMI(London, HVM, EBS)
  implicit case object ami_677f6d03 extends AmazonLinuxAMI(London, HVM, InstanceStore)

  implicit case object ami_c7ee5ca8 extends AmazonLinuxAMI(Frankfurt, HVM, EBS)
  implicit case object ami_feef5d91 extends AmazonLinuxAMI(Frankfurt, HVM, InstanceStore)
  implicit case object ami_66ec5e09 extends AmazonLinuxAMI(Frankfurt, PV,  EBS)
  implicit case object ami_58ec5e37 extends AmazonLinuxAMI(Frankfurt, PV,  InstanceStore)

  implicit case object ami_0797ea64 extends AmazonLinuxAMI(Singapore, HVM, EBS)
  implicit case object ami_ca96eba9 extends AmazonLinuxAMI(Singapore, HVM, InstanceStore)
  implicit case object ami_5a91ec39 extends AmazonLinuxAMI(Singapore, PV,  EBS)
  implicit case object ami_0697ea65 extends AmazonLinuxAMI(Singapore, PV,  InstanceStore)

  implicit case object ami_9bec36f5 extends AmazonLinuxAMI(Seoul, HVM, EBS)
  implicit case object ami_a3ed37cd extends AmazonLinuxAMI(Seoul, HVM, InstanceStore)

  implicit case object ami_2a69be4c extends AmazonLinuxAMI(Tokyo, HVM, EBS)
  implicit case object ami_8068bfe6 extends AmazonLinuxAMI(Tokyo, HVM, InstanceStore)
  implicit case object ami_05964063 extends AmazonLinuxAMI(Tokyo, PV,  EBS)
  implicit case object ami_c59543a3 extends AmazonLinuxAMI(Tokyo, PV,  InstanceStore)

  implicit case object ami_8536d6e7 extends AmazonLinuxAMI(Sydney, HVM, EBS)
  implicit case object ami_8236d6e0 extends AmazonLinuxAMI(Sydney, HVM, InstanceStore)
  implicit case object ami_f137d793 extends AmazonLinuxAMI(Sydney, PV,  EBS)
  implicit case object ami_8f36d6ed extends AmazonLinuxAMI(Sydney, PV,  InstanceStore)

  implicit case object ami_4fc58420 extends AmazonLinuxAMI(Mumbai, HVM, EBS)
  implicit case object ami_e3c2838c extends AmazonLinuxAMI(Mumbai, HVM, InstanceStore)

  implicit case object ami_f1344b9d extends AmazonLinuxAMI(SaoPaulo, HVM, EBS)
  implicit case object ami_21354a4d extends AmazonLinuxAMI(SaoPaulo, HVM, InstanceStore)
  implicit case object ami_b4344bd8 extends AmazonLinuxAMI(SaoPaulo, PV,  EBS)
  implicit case object ami_05364969 extends AmazonLinuxAMI(SaoPaulo, PV,  InstanceStore)

  implicit case object ami_fba67596 extends AmazonLinuxAMI(Beijing, HVM, EBS)
  implicit case object ami_b6a774db extends AmazonLinuxAMI(Beijing, HVM, InstanceStore)
  implicit case object ami_04a57669 extends AmazonLinuxAMI(Beijing, PV,  EBS)
  implicit case object ami_24a67549 extends AmazonLinuxAMI(Beijing, PV,  InstanceStore)

  implicit case object ami_6e9c1e0f extends AmazonLinuxAMI(GovCloud, HVM, EBS)
  implicit case object ami_a89e1cc9 extends AmazonLinuxAMI(GovCloud, HVM, InstanceStore)
  implicit case object ami_139c1e72 extends AmazonLinuxAMI(GovCloud, PV,  EBS)
  implicit case object ami_9c991bfd extends AmazonLinuxAMI(GovCloud, PV,  InstanceStore)

}
