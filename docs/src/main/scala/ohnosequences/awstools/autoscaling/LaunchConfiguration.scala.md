
```scala
package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._

case class LaunchConfiguration(
  val name: String,
  val purchaseModel: AnyPurchaseModel,
  val launchSpecs: AnyLaunchSpecs
) //extends AnyLaunchConfiguration


case object LaunchConfiguration {

  // NOTE: this is an awful conversion
  // FIXME: remove it
  @deprecated("Don't convert java sdk types to the scala ones", since = "v0.15.0")
  def fromAWS(launchConfiguration: amzn.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {

    LaunchConfiguration(
      name = launchConfiguration.getLaunchConfigurationName,
      purchaseModel = stringToOption(launchConfiguration.getSpotPrice) match {
        case None => OnDemand
        case Some(price) => Spot(Some(price.toDouble))
      },
      launchSpecs = LaunchSpecs(
        new AnyInstanceSpecs {
          type InstanceType = AnyInstanceType
          val instanceType: AnyInstanceType =
            InstanceType.fromName(launchConfiguration.getInstanceType)

          type AMI = AnyAMI
          val ami = new AnyAMI {
            val id = launchConfiguration.getImageId
          }
        }
      )(keyName = launchConfiguration.getKeyName,
        securityGroups = launchConfiguration.getSecurityGroups.toList,
        deviceMapping = launchConfiguration.getBlockDeviceMappings.map(m => (m.getDeviceName, m.getVirtualName)).toMap,
        userData = launchConfiguration.getUserData,
        instanceProfile = stringToOption(launchConfiguration.getIamInstanceProfile),
        instanceMonitoring = launchConfiguration.getInstanceMonitoring.isEnabled
      )
    )
  }
}

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md