
```scala
package ohnosequences.awstools.ec2

import ohnosequences.awstools.regions
```

## [Amazon Machine Images (AMI)](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html)

```scala
sealed abstract class Architecture(val wordSize: Int)
case object x86_32 extends Architecture(32)
case object x86_64 extends Architecture(64)
```

### [Linux AMI Virtualization Types](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/virtualization_types.html)

```scala
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
sealed trait AnyStorageType
case object EBS extends AnyStorageType //; type EBS = EBS.type
case object InstanceStore extends AnyStorageType //; type InstanceStore = InstanceStore.type


// TODO: [Launch permissions](http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ComponentsAMIs.html#launch-permissions)

```

Just some base trait:

```scala
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

```




[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md