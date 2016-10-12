package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions

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
trait AnyAMI {

  val id: String
}

trait AnyLinuxAMI extends AnyAMI {

  val version: String

  type Region <: regions.Region
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

  final lazy val id: String = s"ami-${idNum}"

  private lazy val idNum: String = {
    val r: regions.Region = region
    val v: AnyVirtualization = virt
    val s: AnyStorageType = storage
    import regions.Region._

    r match {
      case NorthernVirginia  => v match {
        case HVM => s match {
          case EBS           => "c481fad3"
          case InstanceStore => "4487fc53"
        }
        case PV  => s match {
          case EBS           => "4d87fc5a"
          case InstanceStore => "4287fc55"
        }
      }
      case Oregon => v match {
        case HVM => s match {
          case EBS           => "b04e92d0"
          case InstanceStore => "dd4894bd"
        }
        case PV  => s match {
          case EBS           => "1d49957d"
          case InstanceStore => "48499528"
        }
      }
      case NorthernCalifornia => v match {
        case HVM => s match {
          case EBS           => "de347abe"
          case InstanceStore => "9e3779fe"
        }
        case PV  => s match {
          case EBS           => "df3779bf"
          case InstanceStore => "69367809"
        }
      }
      case Ireland => v match {
        case HVM => s match {
          case EBS           => "d41d58a7"
          case InstanceStore => "64105517"
        }
        case PV  => s match {
          case EBS           => "0e10557d"
          case InstanceStore => "8c1d58ff"
        }
      }
      case Frankfurt => v match {
        case HVM => s match {
          case EBS           => "0044b96f"
          case InstanceStore => "a74ab7c8"
        }
        case PV  => s match {
          case EBS           => "1345b87c"
          case InstanceStore => "f64ab799"
        }
      }
      case Singapore => v match {
        case HVM => s match {
          case EBS           => "7243e611"
          case InstanceStore => "4841e42b"
        }
        case PV  => s match {
          case EBS           => "a743e6c4"
          case InstanceStore => "d846e3bb"
        }
      }
      case Tokyo => v match {
        case HVM => s match {
          case EBS           => "1a15c77b"
          case InstanceStore => "9016c4f1"
        }
        case PV  => s match {
          case EBS           => "cf14c6ae"
          case InstanceStore => "4615c727"
        }
      }
      case Sydney => v match {
        case HVM => s match {
          case EBS           => "55d4e436"
          case InstanceStore => "fbd6e698"
        }
        case PV  => s match {
          case EBS           => "3ad6e659"
          case InstanceStore => "3fd6e65c"
        }
      }
      case SaoPaulo => v match {
        case HVM => s match {
          case EBS           => "b777e4db"
          case InstanceStore => "5075e63c"
        }
        case PV  => s match {
          case EBS           => "1d75e671"
          case InstanceStore => "b477e4d8"
        }
      }
      case Beijing => v match {
        case HVM => s match {
          case EBS           => "fa875397"
          case InstanceStore => "cb8357a6"
        }
        case PV  => s match {
          case EBS           => "d98357b4"
          case InstanceStore => "1a8e5a77"
        }
      }
      case GovCloud => v match {
        case HVM => s match {
          case EBS           => "7b4df41a"
          case InstanceStore => "ae4bf2cf"
        }
        case PV  => s match {
          case EBS           => "144cf575"
          case InstanceStore => "bc48f1dd"
        }
      }
    }
  }
}

case class AmazonLinuxAMI[
  R <: regions.Region,
  V <: AnyVirtualization,
  S <: AnyStorageType
](val region: R,
  val virt: V,
  val storage: S
) extends AnyAmazonLinuxAMI {

  type Region = R
  type Virt = V
  type Storage = S
}
