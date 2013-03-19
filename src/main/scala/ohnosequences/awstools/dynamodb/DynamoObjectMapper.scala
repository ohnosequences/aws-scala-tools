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
