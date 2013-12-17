### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](DynamoDB.md)
            + [DynamoObjectMapper.scala](DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
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
package ohnosequences.awstools.dynamodb

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.datamodeling.{DynamoDBMapperConfig, DynamoDBQueryExpression, DynamoDBMapper}
import com.amazonaws.services.dynamodb.model.{ComparisonOperator, Condition}

import scala.collection.JavaConversions._
import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapperConfig.TableNameOverride

case class DynamoObjectMapper(ddb: AmazonDynamoDBClient, mapper: DynamoDBMapper) {

  def load[T <: Any, H <: KeyType, R <: KeyType](clazz: Class[T], tableName: String, hashKey: KeyValue[H], rangeKey: KeyValue[R], default: T) = {
    val config = new DynamoDBMapperConfig(new TableNameOverride(tableName))
    mapper.load(clazz, hashKey.getValue, rangeKey.getValue, config) match {
      case null => {
        mapper.save(default, config)
        default
      }
      case t => t
    }
  }

  def save[T](tableName: String, t: T) {
    val config = new DynamoDBMapperConfig(new TableNameOverride(tableName))
    mapper.save[T](t, config)
  }

  def queryRangeInterval[T <: Any, H <: KeyType, R <: KeyType](clazz: Class[T], tableName: String, hashKey: KeyValue[H], rangeLowerBound: KeyValue[R], rangeUpperBound: KeyValue[R]): List[T] = {
    val config = new DynamoDBMapperConfig(new TableNameOverride(tableName))
    val result = mapper.query(clazz, new DynamoDBQueryExpression(hashKey.getAttributeValue)
        .withRangeKeyCondition(new Condition()
        .withComparisonOperator(ComparisonOperator.BETWEEN.toString)
        .withAttributeValueList(rangeLowerBound.getAttributeValue, rangeUpperBound.getAttributeValue)
      ),
      config
    )
    result.toList
  }

}

```

