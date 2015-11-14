
```scala
package ohnosequences.awstools.regions

import com.amazonaws.regions.{Regions => JavaRegions}
import com.amazonaws.regions.{Region => JavaRegion}

sealed abstract class Region(val name: String) {
  override def toString = name

  def toAWSRegions: JavaRegions = JavaRegions.fromName(name)
  def toAWSRegion: JavaRegion = JavaRegion.getRegion(this.toAWSRegions)
}

case object Region {

  // The same names as in the Java AWS SDK
  case object AP_NORTHEAST_1 extends Region("ap-northeast-1") // Tokyo
  case object AP_SOUTHEAST_1 extends Region("ap-southeast-1") // Singapore
  case object AP_SOUTHEAST_2 extends Region("ap-southeast-2") // Sydney
  case object EU_WEST_1      extends Region("eu-west-1")      // Ireland
  case object EU_CENTRAL_1   extends Region("eu-central-1")   // Frankfurt
  case object SA_EAST_1      extends Region("sa-east-1")      // São Paulo
  case object US_EAST_1      extends Region("us-east-1")      // Northern Virginia
  case object US_WEST_1      extends Region("us-west-1")      // Northern California
  case object US_WEST_2      extends Region("us-west-2")      // Oregon
  case object CN_NORTH_1     extends Region("cn-north-1")     // Beijing
  case object GovCloud       extends Region("us-gov-west-1")  // Secret cloud for CIA


  // Nice geographical synonims:
  val Tokyo              = AP_NORTHEAST_1
  val Singapore          = AP_SOUTHEAST_1
  val Sydney             = AP_SOUTHEAST_2
  val Ireland            = EU_WEST_1
  val Frankfurt          = EU_CENTRAL_1
  val SaoPaulo           = SA_EAST_1
  val NorthernVirginia   = US_EAST_1
  val NorthernCalifornia = US_WEST_1
  val Oregon             = US_WEST_2
  val Beijing            = CN_NORTH_1
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
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md