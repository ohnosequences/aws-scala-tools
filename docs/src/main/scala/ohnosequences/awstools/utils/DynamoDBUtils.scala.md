
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




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md