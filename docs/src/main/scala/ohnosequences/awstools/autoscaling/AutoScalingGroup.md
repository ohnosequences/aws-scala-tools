### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](AutoScaling.md)
            + [AutoScalingGroup.scala](AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
          + regions
            + [Region.scala](../regions/Region.md)
          + s3
            + [Bucket.scala](../s3/Bucket.md)
            + [S3.scala](../s3/S3.md)
          + sns
            + [SNS.scala](../sns/SNS.md)
            + [Topic.scala](../sns/Topic.md)
          + sqs
            + [Queue.scala](../sqs/Queue.md)
            + [SQS.scala](../sqs/SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.md)
          + [EC2Tests.scala](../../../../../test/scala/ohnosequences/awstools/EC2Tests.md)
          + [S3Tests.scala](../../../../../test/scala/ohnosequences/awstools/S3Tests.md)
          + [SNSTests.scala](../../../../../test/scala/ohnosequences/awstools/SNSTests.md)
          + [SQSTests.scala](../../../../../test/scala/ohnosequences/awstools/SQSTests.md)

------


```scala
package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2.{EC2, Utils, InstanceType, InstanceSpecs}
import scala.collection.JavaConversions._
import java.util.Date

case class AutoScalingGroup(
  name: String = "",
  launchingConfiguration: LaunchConfiguration,
  minSize: Int,
  maxSize: Int,
  desiredCapacity: Int,
  availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
) {

}

sealed abstract class PurchaseModel

case object OnDemand extends PurchaseModel

case object SpotAuto extends PurchaseModel {
  def getCurrentPrice(ec2: EC2, instanceType: InstanceType): Double = {
    ec2.getCurrentSpotPrice(instanceType) + 0.001
  }
}

case class Spot(price: Double) extends PurchaseModel


case class LaunchConfiguration(
  name: String = "",
  purchaseModel: PurchaseModel,
  instanceSpecs: InstanceSpecs
)


object LaunchConfiguration {

  def fromAWS(launchConfiguration: com.amazonaws.services.autoscaling.model.LaunchConfiguration): LaunchConfiguration = {

    LaunchConfiguration(
      name = launchConfiguration.getLaunchConfigurationName,

      purchaseModel = Utils.stringToOption(launchConfiguration.getSpotPrice) match {
        case None => OnDemand
        case Some(price) => Spot(price.toDouble)
      },
      instanceSpecs = InstanceSpecs(
        instanceType = InstanceType.fromName(launchConfiguration.getInstanceType),
        amiId = launchConfiguration.getImageId,
        keyName = launchConfiguration.getKeyName,
        securityGroups = launchConfiguration.getSecurityGroups.toList,
        deviceMapping = launchConfiguration.getBlockDeviceMappings.map(m => (m.getDeviceName, m.getVirtualName)).toMap,
        userData = launchConfiguration.getUserData,
        instanceProfile =  Utils.stringToOption(launchConfiguration.getIamInstanceProfile)
      )
    )
  }
}

object AutoScalingGroup {
  def fromAWS(autoScalingGroup: com.amazonaws.services.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
    autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
      case None => None;//since launch configuration deleted this autoscaling group will be deleted soon
      case Some(launchConfiguration) => Some(AutoScalingGroup(
        name = autoScalingGroup.getAutoScalingGroupName,
        launchingConfiguration = launchConfiguration,
        minSize = autoScalingGroup.getMinSize,
        maxSize = autoScalingGroup.getMaxSize,
        desiredCapacity = autoScalingGroup.getDesiredCapacity

      ))
    }
  }
}

```

