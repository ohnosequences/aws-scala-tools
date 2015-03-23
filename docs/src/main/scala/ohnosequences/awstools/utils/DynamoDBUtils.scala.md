
```scala
package ohnosequences.awstools.utils

import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.{DeleteItemRequest, ProvisionedThroughputExceededException, AttributeValue, GetItemRequest}
import ohnosequences.logging.Logger

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}
import scala.collection.JavaConversions._

object DynamoDBUtils {


  //with repeats
  def getItem(ddb: AmazonDynamoDB,
              tableName: String,
              key: Map[String, AttributeValue],
              attributesToGet: Seq[String],
              logger: Logger): Try[Map[String, AttributeValue]] = {

    @tailrec
    def getItemRec(): Try[Map[String, AttributeValue]] = {
      try {
        val rawItem = ddb.getItem(new GetItemRequest()
          .withTableName(tableName)
          .withKey(key)
          .withAttributesToGet(attributesToGet)
        ).getItem
        if (rawItem != null) {
          Success(rawItem.toMap)
        } else {
          Failure(new NullPointerException)
        }
      } catch {
        case p: ProvisionedThroughputExceededException => {
          getItemRec()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    getItemRec()
  }

  //with repeats
  def deleteItem(ddb: AmazonDynamoDB,
                 tableName: String,
                 key: Map[String, AttributeValue],
                 logger: Logger): Try[Unit] = {

    @tailrec
    def deleteItemRep(): Try[Unit] = {
      try {
        val rawItem = ddb.deleteItem(new DeleteItemRequest()
          .withTableName(tableName)
          .withKey(key)
        )
        Success(())
      } catch {
        case p: ProvisionedThroughputExceededException => {
          deleteItemRep()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    deleteItemRep()
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

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md