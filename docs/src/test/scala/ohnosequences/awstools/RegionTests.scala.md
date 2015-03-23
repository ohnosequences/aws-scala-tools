
```scala
package ohnosequences.awstools.regions

import org.junit.Test
import org.junit.Assert._

import com.amazonaws.regions.{Regions => JavaRegions}
import com.amazonaws.regions.{Region => JavaRegion}

import ohnosequences.awstools.regions._
import ohnosequences.awstools.regions.Region._

class RegionTests {

  @Test
  def toJavaRegionsTest() {
    assertEquals(toJavaRegions(Tokyo),              JavaRegions.AP_NORTHEAST_1)
    assertEquals(toJavaRegions(Singapore),          JavaRegions.AP_SOUTHEAST_1)
    assertEquals(toJavaRegions(Sydney),             JavaRegions.AP_SOUTHEAST_2)
    assertEquals(toJavaRegions(Ireland),            JavaRegions.EU_WEST_1)
    assertEquals(toJavaRegions(SaoPaulo),           JavaRegions.SA_EAST_1)
    assertEquals(toJavaRegions(NorthernVirginia),   JavaRegions.US_EAST_1)
    assertEquals(toJavaRegions(NorthernCalifornia), JavaRegions.US_WEST_1)
    assertEquals(toJavaRegions(Oregon),             JavaRegions.US_WEST_2)
    assertEquals(toJavaRegions(GovCloud),           JavaRegions.GovCloud)
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
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../../../../main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../../../../main/scala/ohnosequences/awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../../../../main/scala/ohnosequences/awstools/s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../../../../main/scala/ohnosequences/awstools/s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../../../../main/scala/ohnosequences/awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../../../../main/scala/ohnosequences/awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../../../main/scala/ohnosequences/logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../../../main/scala/ohnosequences/logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: SQSTests.scala.md