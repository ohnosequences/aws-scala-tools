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

// Amazon Linux AMI 2017.09.1
// See http://aws.amazon.com/amazon-linux-ami/
trait AnyAmazonLinuxAMI extends AnyLinuxAMI {

  final val version: String = "2017.09.1"

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

  implicit case object ami_55ef662f extends AmazonLinuxAMI(NorthernVirginia, HVM, EBS)
  implicit case object ami_f6ed648c extends AmazonLinuxAMI(NorthernVirginia, HVM, InstanceStore)
  implicit case object ami_f0ea638a extends AmazonLinuxAMI(NorthernVirginia, PV,  EBS)
  implicit case object ami_c4ea63be extends AmazonLinuxAMI(NorthernVirginia, PV,  InstanceStore)

  implicit case object ami_15e9c770 extends AmazonLinuxAMI(Ohio, HVM, EBS)
  implicit case object ami_12e9c777 extends AmazonLinuxAMI(Ohio, HVM, InstanceStore)

  implicit case object ami_bf4193c7 extends AmazonLinuxAMI(Oregon, HVM, EBS)
  implicit case object ami_dd4496a5 extends AmazonLinuxAMI(Oregon, HVM, InstanceStore)
  implicit case object ami_da4694a2 extends AmazonLinuxAMI(Oregon, PV,  EBS)
  implicit case object ami_594a9821 extends AmazonLinuxAMI(Oregon, PV,  InstanceStore)

  implicit case object ami_a51f27c5 extends AmazonLinuxAMI(NorthernCalifornia, HVM, EBS)
  implicit case object ami_941d25f4 extends AmazonLinuxAMI(NorthernCalifornia, HVM, InstanceStore)
  implicit case object ami_3b1e265b extends AmazonLinuxAMI(NorthernCalifornia, PV,  EBS)
  implicit case object ami_c91c24a9 extends AmazonLinuxAMI(NorthernCalifornia, PV,  InstanceStore)

  implicit case object ami_d29e25b6 extends AmazonLinuxAMI(CanadaCentral, HVM, EBS)
  implicit case object ami_c09f24a4 extends AmazonLinuxAMI(CanadaCentral, HVM, InstanceStore)

  implicit case object ami_1a962263 extends AmazonLinuxAMI(Ireland, HVM, EBS)
  implicit case object ami_7690240f extends AmazonLinuxAMI(Ireland, HVM, InstanceStore)
  implicit case object ami_889622f1 extends AmazonLinuxAMI(Ireland, PV,  EBS)
  implicit case object ami_bd9723c4 extends AmazonLinuxAMI(Ireland, PV,  InstanceStore)

  implicit case object ami_e7d6c983 extends AmazonLinuxAMI(London, HVM, EBS)
  implicit case object ami_29d5ca4d extends AmazonLinuxAMI(London, HVM, InstanceStore)

  implicit case object ami_bf2ba8d0 extends AmazonLinuxAMI(Frankfurt, HVM, EBS)
  implicit case object ami_2828ab47 extends AmazonLinuxAMI(Frankfurt, HVM, InstanceStore)
  implicit case object ami_2928ab46 extends AmazonLinuxAMI(Frankfurt, PV,  EBS)
  implicit case object ami_5e29aa31 extends AmazonLinuxAMI(Frankfurt, PV,  InstanceStore)

  implicit case object ami_c63d6aa5 extends AmazonLinuxAMI(Singapore, HVM, EBS)
  implicit case object ami_a53e69c6 extends AmazonLinuxAMI(Singapore, HVM, InstanceStore)
  implicit case object ami_3f3d6a5c extends AmazonLinuxAMI(Singapore, PV,  EBS)
  implicit case object ami_803d6ae3 extends AmazonLinuxAMI(Singapore, PV,  InstanceStore)

  implicit case object ami_1196317f extends AmazonLinuxAMI(Seoul, HVM, EBS)
  implicit case object ami_2d953243 extends AmazonLinuxAMI(Seoul, HVM, InstanceStore)

  implicit case object ami_da9e2cbc extends AmazonLinuxAMI(Tokyo, HVM, EBS)
  implicit case object ami_d5992bb3 extends AmazonLinuxAMI(Tokyo, HVM, InstanceStore)
  implicit case object ami_99982aff extends AmazonLinuxAMI(Tokyo, PV,  EBS)
  implicit case object ami_9a9e2cfc extends AmazonLinuxAMI(Tokyo, PV,  InstanceStore)

  implicit case object ami_ff4ea59d extends AmazonLinuxAMI(Sydney, HVM, EBS)
  implicit case object ami_3248a350 extends AmazonLinuxAMI(Sydney, HVM, InstanceStore)
  implicit case object ami_3249a250 extends AmazonLinuxAMI(Sydney, PV,  EBS)
  implicit case object ami_f24ea590 extends AmazonLinuxAMI(Sydney, PV,  InstanceStore)

  implicit case object ami_d5c18eba extends AmazonLinuxAMI(Mumbai, HVM, EBS)
  implicit case object ami_4dc08f22 extends AmazonLinuxAMI(Mumbai, HVM, InstanceStore)

  implicit case object ami_286f2a44 extends AmazonLinuxAMI(SaoPaulo, HVM, EBS)
  implicit case object ami_716e2b1d extends AmazonLinuxAMI(SaoPaulo, HVM, InstanceStore)
  implicit case object ami_b96c29d5 extends AmazonLinuxAMI(SaoPaulo, PV,  EBS)
  implicit case object ami_de6e2bb2 extends AmazonLinuxAMI(SaoPaulo, PV,  InstanceStore)

  implicit case object ami_dadb09b7 extends AmazonLinuxAMI(Beijing, HVM, EBS)
  implicit case object ami_f9d50794 extends AmazonLinuxAMI(Beijing, HVM, InstanceStore)
  implicit case object ami_8ed80ae3 extends AmazonLinuxAMI(Beijing, PV,  EBS)
  implicit case object ami_c0da08ad extends AmazonLinuxAMI(Beijing, PV,  InstanceStore)

  implicit case object ami_f562ee94 extends AmazonLinuxAMI(GovCloud, HVM, EBS)
  implicit case object ami_cb63efaa extends AmazonLinuxAMI(GovCloud, HVM, InstanceStore)
  implicit case object ami_cc63efad extends AmazonLinuxAMI(GovCloud, PV,  EBS)
  implicit case object ami_2d7cf04c extends AmazonLinuxAMI(GovCloud, PV,  InstanceStore)

}
