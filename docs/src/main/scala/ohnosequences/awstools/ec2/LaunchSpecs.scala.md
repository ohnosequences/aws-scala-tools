
```scala
package ohnosequences.awstools.ec2

import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._


trait AnyLaunchSpecs {
  type InstanceSpecs <: AnyInstanceSpecs
  val  instanceSpecs: InstanceSpecs

  // poorly-typed params:
  val keyName: String
  val userData: String
  val instanceProfile: Option[String]
  val securityGroups: List[String]
  val instanceMonitoring: Boolean
  val deviceMapping: Map[String, String]

  final def toAWS: amzn.ec2.model.LaunchSpecification = {
    val ls = new amzn.ec2.model.LaunchSpecification()
      .withSecurityGroups(this.securityGroups)
      .withInstanceType(this.instanceSpecs.instanceType.toAWS)
      .withImageId(this.instanceSpecs.ami.id)
      .withKeyName(this.keyName)
      .withMonitoringEnabled(this.instanceMonitoring)
      .withBlockDeviceMappings(this.deviceMapping.map{ case (key, value) =>
        new amzn.ec2.model.BlockDeviceMapping()
          .withDeviceName(key)
          .withVirtualName(value)
      })
      .withUserData(base64encode(this.userData))

    this.instanceProfile match {
      case Some(name) => ls.withIamInstanceProfile(new amzn.ec2.model.IamInstanceProfileSpecification().withName(name))
      case None => ls
    }
  }

}

// case object AnyLaunchSpecs {
//   implicit def getAWSLaunchSpecs(specs: AnyLaunchSpecs):
//     amzn.ec2.model.LaunchSpecification = specs.toAWS
// }

case class LaunchSpecs[
  S <: AnyInstanceSpecs
](val instanceSpecs: S)(
  val keyName: String,
  val userData: String = "",
  val instanceProfile: Option[String] = None,
  val securityGroups: List[String] = List(),
  val instanceMonitoring: Boolean = false,
  val deviceMapping: Map[String, String] = Map[String, String]()
) extends AnyLaunchSpecs {
  type InstanceSpecs = S
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