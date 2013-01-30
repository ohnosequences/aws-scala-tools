package ohnosequences.awstools.dynamodb

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.datamodeling.{DynamoDBQueryExpression, DynamoDBMapper}
import com.amazonaws.services.dynamodb.model.{ComparisonOperator, Condition, AttributeValue}

import scala.collection.JavaConversions._

case class DynamoObjectMapper(ddb: AmazonDynamoDBClient, mapper: DynamoDBMapper) {

  def load[T <: Any, H <: KeyType, R <: KeyType](clazz: Class[T], hashKey: KeyValue[H], rangeKey: KeyValue[R], default: T) = {
    mapper.load(clazz, hashKey.getValue, rangeKey.getValue) match {
      case null => {
        mapper.save(default)
        default
      }
      case t => t
    }
  }

  def save[T](t: T) {
    mapper.save[T](t)
  }

  def queryRangeInterval[T <: Any, H <: KeyType, R <: KeyType](clazz: Class[T], hashKey: KeyValue[H], rangeLowerBound: KeyValue[R], rangeUpperBound: KeyValue[R]): List[T] = {

    val result = mapper.query(clazz, new DynamoDBQueryExpression(hashKey.getAttributeValue)
      .withRangeKeyCondition(new Condition()
      .withComparisonOperator(ComparisonOperator.BETWEEN.toString)
      .withAttributeValueList(rangeLowerBound.getAttributeValue, rangeUpperBound.getAttributeValue)
    )
    )
    result.toList
  }

//  def getAttributeValue(any: Any) = any match {
//    case n: Long => new AttributeValue().withN(String.valueOf(n))
//    case n: Int => new AttributeValue().withN(String.valueOf(n))
//    case other => new AttributeValue().withS(other.toString)
//  }


}
