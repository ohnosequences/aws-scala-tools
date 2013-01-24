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

//abstract sealed class DynamoValue
//
//case class LongValue(value: Long) extends DynamoValue


class Table[T](ddb: AmazonDynamoDBClient, name: String, val hashKeyName: String, val rangeKeyName: String) {

  table: AttributeValueBijection[T] =>

  case class Item(map: Map[String, T]) {
    def delete {
      deleteItem(new Key(table.to(map(hashKeyName)), table.to(map(rangeKeyName))))
    }
  }



  implicit def TToAttributeValue(t: T) = to(t)
  implicit def AttributeToT(t: AttributeValue) = from(t)


  def putItem(item: Map[String, T]) {
    ddb.putItem(new PutItemRequest(name, item.mapValues(to)))
  }

  def deleteItem(key: Key) {
    ddb.deleteItem(new DeleteItemRequest(name, key))
  }

  def getItem(key: Key) = {
    var map = ddb.getItem(new GetItemRequest()
      .withTableName(name)
      .withKey(key)
    ).getItem
    if (map == null) {
      Map[String, T]()
    } else {
      map.mapValues(from)
    }
  }





  def query(hashKeyValue: T, rangeKeyLowerBound: T, rangeKeyUpperBound: T): List[Map[String, T]] = {
    // "42"

    ddb.query(
      new QueryRequest()
        .withHashKeyValue(hashKeyValue)
        .withTableName(name)

        .withRangeKeyCondition(new Condition()
          .withComparisonOperator(ComparisonOperator.BETWEEN.toString)
          .withAttributeValueList(rangeKeyLowerBound, rangeKeyUpperBound)
        )

     ).getItems.map(_.toMap.mapValues(from(_))).toList

  }

//  def scan() = {
//    val items = ddb.scan(new ScanRequest(name)).getItems
//    items.map{item => Item(item.mapValues(from(_)))}
//  }
  def getKey(item: java.util.Map[String, AttributeValue]) = {
    new Key(item(hashKeyName), item(rangeKeyName))
  }

  def getKey2(item: java.util.Map[String, T]) = {
    new Key(item(hashKeyName), item(rangeKeyName))
  }



  //rewrite!!!!!!!!!!!!
  def clear() {

    var items: java.util.List[java.util.Map[String, AttributeValue]] = List()

    do {
      println("deleting " + items.size() + " items")
      items.foreach{item: java.util.Map[String, AttributeValue] =>
        print(".")
        deleteItem(getKey(item))
      }
      println()
      items = ddb.scan(new ScanRequest(name)).getItems

    } while(!items.isEmpty)

  }
}


