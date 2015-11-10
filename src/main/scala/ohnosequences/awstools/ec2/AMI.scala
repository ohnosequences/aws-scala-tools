package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions

/* ## [Amazon Machine Images (AMI)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html) */


sealed abstract class Architecture(wordSize: Int)
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

// Amazon Linux AMI 2015.09
// See http://aws.amazon.com/amazon-linux-ami/
trait AnyAmazonLinuxAMI extends AnyLinuxAMI {

  final val version: String = "2015.09"

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
          case EBS           => "e3106686"
          case InstanceStore => "65116700"
        }
        case PV  => s match {
          case EBS           => "cf1066aa"
          case InstanceStore => "971066f2"
        }
      }
      case Oregon => v match {
        case HVM => s match {
          case EBS           => "9ff7e8af"
          case InstanceStore => "bbf7e88b"
        }
        case PV  => s match {
          case EBS           => "81f7e8b1"
          case InstanceStore => "bdf7e88d"
        }
      }
      case NorthernCalifornia => v match {
        case HVM => s match {
          case EBS           => "cd3aff89"
          case InstanceStore => "d53aff91"
        }
        case PV  => s match {
          case EBS           => "d53aff91"
          case InstanceStore => "c93aff8d"
        }
      }
      case Ireland => v match {
        case HVM => s match {
          case EBS           => "69b9941e"
          case InstanceStore => "7db9940a"
        }
        case PV  => s match {
          case EBS           => "a3be93d4"
          case InstanceStore => "8fbe93f8"
        }
      }
      case Frankfurt => v match {
        case HVM => s match {
          case EBS           => "daaeaec7"
          case InstanceStore => "a2aeaebf"
        }
        case PV  => s match {
          case EBS           => "a6aeaebb"
          case InstanceStore => "a0aeaebd"
        }
      }
      case Singapore => v match {
        case HVM => s match {
          case EBS           => "52978200"
          case InstanceStore => "ac9481fe"
        }
        case PV  => s match {
          case EBS           => "50978202"
          case InstanceStore => "4c97821e"
        }
      }
      case Tokyo => v match {
        case HVM => s match {
          case EBS           => "9a2fb89a"
          case InstanceStore => "a22fb8a2"
        }
        case PV  => s match {
          case EBS           => "9c2fb89c"
          case InstanceStore => "a42fb8a4"
        }
      }
      case Sydney => v match {
        case HVM => s match {
          case EBS           => "c11856fb"
          case InstanceStore => "871856bd"
        }
        case PV  => s match {
          case EBS           => "c71856fd"
          case InstanceStore => "851856bf"
        }
      }
        case SaoPaulo => v match {
        case HVM => s match {
          case EBS           => "3b0c9926"
          case InstanceStore => "030c991e"
        }
        case PV  => s match {
          case EBS           => "370c992a"
          case InstanceStore => "010c991c"
        }
      }
      case Beijing => v match {
        case HVM => s match {
          case EBS           => "6cb22e55"
          case InstanceStore => "76b22e4f"
        }
        case PV  => s match {
          case EBS           => "54b22e6d"
          case InstanceStore => "68b22e51"
        }
      }
      case GovCloud => v match {
        case HVM => s match {
          case EBS           => "ad34568e"
          case InstanceStore => "a5345686"
        }
        case PV  => s match {
          case EBS           => "b3345690"
          case InstanceStore => "ab345688"
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
