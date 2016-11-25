
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model._
import com.amazonaws.services.autoscaling.{ model => as }
import scala.collection.JavaConversions._
```

This type is a common denominator type for various types from the SDK desribing launch specification used for on-demand or spot requests in EC2 and for launch configurations in AutoScaling. Motivation:

- having a more convenient way to construct these specifications
- better types for the parameters
- compile time checks for parameters compatibility where possible


```scala
trait AnyLaunchSpecs {

  type InstanceType <: AnyInstanceType
  val  instanceType: InstanceType

  type AMI <: AnyLinuxAMI
  val  ami: AMI
```

We want to ensure that tye instance type supports the given AMI at compile time

```scala
  implicit val supportsAMI: InstanceType SupportsAMI AMI
```

The name of the key pair

```scala
  val keyName: String
```

The user data to make available to the launched EC2 instances

```scala
  val userData: String
```

The name of the instance profile associated with the IAM role for the instance

```scala
  val iamProfileName: Option[String]
```

Enables detailed monitoring (true) or basic monitoring (false) for the Auto Scaling instances

```scala
  val monitoring: Boolean
```

One or more security groups with which to associate the instances

```scala
  val securityGroups: Set[String]
```

One or more mappings that specify how block devices are exposed to the instance

```scala
  val deviceMappings: Map[String, String]
```

Indicates whether the instance is optimized for Amazon EBS I/O

```scala
  val ebsOptimized: Boolean
```

The ID of the kernel (associated with the AMI)

```scala
  val kernelId: Option[String]
```

The ID of the RAM disk associated with the AMI

```scala
  val ramdiskId: Option[String]
}

case class LaunchSpecs[
  T <: AnyInstanceType,
  A <: AnyLinuxAMI
](val ami: A,
  val instanceType: T,
  val keyName: String,
  val userData: String,
  val iamProfileName: Option[String],
  val monitoring: Boolean = false,
  val securityGroups: Set[String] = Set(),
  val deviceMappings: Map[String, String] = Map(),
  val ebsOptimized: Boolean = false,
  val kernelId: Option[String] = None,
  val ramdiskId: Option[String] = None
)(implicit
  val supportsAMI: T SupportsAMI A
) extends AnyLaunchSpecs {

  type AMI = A
  type InstanceType = T
}



case object AnyLaunchSpecs {
```

Here follows some boring repetitive code for converting LaunchSpecs type to other types from SDK

```scala
  implicit class LaunchSpecsOps(val launchSpecs: AnyLaunchSpecs) extends AnyVal {
```

[`RunInstancesRequest`](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/RunInstancesRequest.html) used to launch on-demand EC2 instances

```scala
    def toRunInstancesRequest: RunInstancesRequest = {

      val request = new RunInstancesRequest()
        .withImageId(launchSpecs.ami.id)
        .withInstanceType(launchSpecs.instanceType)
        .withUserData(base64encode(launchSpecs.userData))
        .withSecurityGroups(launchSpecs.securityGroups)
        .withKeyName(launchSpecs.keyName)
        .withMonitoring(launchSpecs.monitoring)
        .withEbsOptimized(launchSpecs.ebsOptimized)
        .withBlockDeviceMappings(
          launchSpecs.deviceMappings.map { case (key, value) =>
            new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
          }
        )

      launchSpecs.iamProfileName.fold() { name =>
        request.setIamInstanceProfile(new IamInstanceProfileSpecification().withName(name))
      }

      launchSpecs.kernelId.fold()  { request.setKernelId }
      launchSpecs.ramdiskId.fold() { request.setRamdiskId }

      request
    }
```

[`LaunchSpecification`](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/LaunchSpecification.html) used in [`RequestSpotInstancesRequest`](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/ec2/model/RequestSpotInstancesRequest.html) from EC2

```scala
    def toLaunchSpecification: LaunchSpecification = {

      val ls = new LaunchSpecification()
        .withImageId(launchSpecs.ami.id)
        .withInstanceType(launchSpecs.instanceType)
        .withUserData(base64encode(launchSpecs.userData))
        .withSecurityGroups(launchSpecs.securityGroups)
        .withKeyName(launchSpecs.keyName)
        .withMonitoringEnabled(launchSpecs.monitoring)
        .withEbsOptimized(launchSpecs.ebsOptimized)
        .withBlockDeviceMappings(
          launchSpecs.deviceMappings.map { case (key, value) =>
            new BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
          }
        )

      launchSpecs.iamProfileName.fold() { name =>
        ls.setIamInstanceProfile(new IamInstanceProfileSpecification().withName(name))
      }

      launchSpecs.kernelId.fold()  { ls.setKernelId }
      launchSpecs.ramdiskId.fold() { ls.setRamdiskId }

      ls
    }
```

[`CreateLaunchConfigurationRequest`](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/autoscaling/model/CreateLaunchConfigurationRequest.html) used to create launch configuration for autoscaling groups

```scala
    def toCreateLaunchConfigurationRequest: as.CreateLaunchConfigurationRequest = {

      val newRequest = new as.CreateLaunchConfigurationRequest()
        .withImageId(launchSpecs.ami.id)
        .withInstanceType(launchSpecs.instanceType.toString)
        .withUserData(base64encode(launchSpecs.userData))
        .withKeyName(launchSpecs.keyName)
        .withSecurityGroups(launchSpecs.securityGroups)
        .withInstanceMonitoring(new as.InstanceMonitoring().withEnabled(launchSpecs.monitoring))
        .withEbsOptimized(launchSpecs.ebsOptimized)
        .withBlockDeviceMappings(
          launchSpecs.deviceMappings.map { case (key, value) =>
            new as.BlockDeviceMapping().withDeviceName(key).withVirtualName(value)
          }
        )

      launchSpecs.iamProfileName.fold() { name =>
        newRequest.setIamInstanceProfile(name)
      }

      launchSpecs.kernelId.fold()  { newRequest.setKernelId }
      launchSpecs.ramdiskId.fold() { newRequest.setRamdiskId }

      newRequest
    }

  }

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