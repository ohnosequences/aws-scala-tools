
```scala
package ohnosequences.benchmark

import ohnosequences.logging.Logger

import scala.collection.mutable

trait AnyBench {
  def register(label: String, time: Long): Unit

  def averageTime(label: String): Double

  def averageSpeed(label: String): Double

  def totalTime(label: String): Long


}

class Bench extends AnyBench {
  val counts = new mutable.HashMap[String, Int]()
  val times = new mutable.HashMap[String, Long]()

  override def register(label: String, time: Long): Unit = {
    counts.get(label) match {
      case None => counts.put(label, 1)
      case Some(c) => counts.put(label, c + 1)
    }

    times.get(label) match {
      case None => times.put(label, time)
      case Some(t) => times.put(label, t + time)
    }
  }

  override def averageTime(label: String): Double = {
    val time = times.getOrElse(label, 0L)
    val count = counts.getOrElse(label, 0)

    if(count == 0) {
      Double.NaN
    } else {
      (time + 0.0) / count
    }
  }

  override def averageSpeed(label: String): Double = {
    val time = times.getOrElse(label, 0L)
    val count = counts.getOrElse(label, 0)

    if(time == 0L) {
      Double.NaN
    } else {
      (count + 0.0) / time
    }
  }

  override def totalTime(label: String): Long = {
    times.getOrElse(label, 0L)
  }

  def printStats(logger: Logger): Unit = {
    times.keySet.foreach { key =>
      logger.info(key + ": " + averageTime(key) + " ms")
    }

  }
}

```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]
            + [AutoScalingGroup.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]
          + [AWSClients.scala][main\scala\ohnosequences\awstools\AWSClients.scala]
          + dynamodb
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]
          + ec2
            + [EC2.scala][main\scala\ohnosequences\awstools\ec2\EC2.scala]
            + [Filters.scala][main\scala\ohnosequences\awstools\ec2\Filters.scala]
            + [InstanceType.scala][main\scala\ohnosequences\awstools\ec2\InstanceType.scala]
            + [Utils.scala][main\scala\ohnosequences\awstools\ec2\Utils.scala]
          + regions
            + [Region.scala][main\scala\ohnosequences\awstools\regions\Region.scala]
          + s3
            + [Bucket.scala][main\scala\ohnosequences\awstools\s3\Bucket.scala]
            + [S3.scala][main\scala\ohnosequences\awstools\s3\S3.scala]
          + sns
            + [SNS.scala][main\scala\ohnosequences\awstools\sns\SNS.scala]
            + [Topic.scala][main\scala\ohnosequences\awstools\sns\Topic.scala]
          + sqs
            + [Queue.scala][main\scala\ohnosequences\awstools\sqs\Queue.scala]
            + [SQS.scala][main\scala\ohnosequences\awstools\sqs\SQS.scala]
          + utils
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]
            + [SQSUtils.scala][main\scala\ohnosequences\awstools\utils\SQSUtils.scala]
        + benchmark
          + [Benchmark.scala][main\scala\ohnosequences\benchmark\Benchmark.scala]
        + logging
          + [Logger.scala][main\scala\ohnosequences\logging\Logger.scala]
          + [S3Logger.scala][main\scala\ohnosequences\logging\S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [EC2Tests.scala][test\scala\ohnosequences\awstools\EC2Tests.scala]
          + [InstanceTypeTests.scala][test\scala\ohnosequences\awstools\InstanceTypeTests.scala]
          + [RegionTests.scala][test\scala\ohnosequences\awstools\RegionTests.scala]
          + [S3Tests.scala][test\scala\ohnosequences\awstools\S3Tests.scala]
          + [SQSTests.scala][test\scala\ohnosequences\awstools\SQSTests.scala]
          + [TestCredentials.scala][test\scala\ohnosequences\awstools\TestCredentials.scala]

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\awstools\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\awstools\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\awstools\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\awstools\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\awstools\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\awstools\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\awstools\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\awstools\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\awstools\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\awstools\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\awstools\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\awstools\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\awstools\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\awstools\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\awstools\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\awstools\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\awstools\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md