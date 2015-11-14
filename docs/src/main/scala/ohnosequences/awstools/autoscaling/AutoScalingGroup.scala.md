
```scala
package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._
import java.util.Date

case class AutoScalingGroupSize(
  min: Int,
  desired: Int,
  max: Int
)

case class AutoScalingGroup(
  val launchConfiguration: LaunchConfiguration,
  val name: String,
  val size: AutoScalingGroupSize,
  // FIXME: this shouldn't be so explicit:
  val availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
)

case object AutoScalingGroup {

  def fromAWS(autoScalingGroup: amzn.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
    autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
      case None => None //since launch configuration deleted this autoscaling group will be deleted soon
      case Some(launchConfiguration) => Some(AutoScalingGroup(
        launchConfiguration,
        name = autoScalingGroup.getAutoScalingGroupName,
        size = AutoScalingGroupSize(
          autoScalingGroup.getMinSize,
          autoScalingGroup.getDesiredCapacity,
          autoScalingGroup.getMaxSize
        )
      ))
    }
  }
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
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md