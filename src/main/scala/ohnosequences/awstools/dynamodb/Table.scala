package ohnosequences.awstools.dynamodb

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model._

import scala.collection.JavaConversions._

trait AttributeValueBijection[T] {
  def to(t: T): AttributeValue
  def from(attributeValue: AttributeValue): T
}

trait AttributeValueLongBijection extends AttributeValueBijection[Long] {
  override def to(n: Long) = new AttributeValue().withN(n.toString)
  override def from(attributeValue: AttributeValue) = attributeValue.getN.toLong
}

class Table[T](ddb: AmazonDynamoDBClient, name: String) {

  this: AttributeValueBijection[T] =>

  implicit def TToAttributeValue(t: T) = to(t)

  def putItem(item: Map[String, T]) {

    ddb.putItem(new PutItemRequest(name, item.mapValues(to)))
  }

  def query(hashKeyValue: T, rangeKeyLowerBound: T, rangeKeyUpperBound: T) = {
    // "42"
    ddb.query(
      new QueryRequest()
        .withHashKeyValue(hashKeyValue)
        .withTableName(name)

        .withRangeKeyCondition(new Condition()
          .withComparisonOperator(ComparisonOperator.BETWEEN.toString)
          .withAttributeValueList(rangeKeyLowerBound, rangeKeyUpperBound)
        )

     ).getItems

  }



}
