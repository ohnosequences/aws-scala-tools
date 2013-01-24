package ohnosequences.awstools.dynamodb

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model._

import scala.collection.JavaConversions._

class DynamoDB(val ddb: AmazonDynamoDBClient) {

  def shutdown() {
    ddb.shutdown()
  }

//  def query() = {
//   // "42"
//    val items = ddb.query(
//      new QueryRequest()
//        .withHashKeyValue(new AttributeValue().withN("1"))
//        .withTableName("InstancesStat")
//        .withRangeKeyCondition(new Condition()
//          .withComparisonOperator(ComparisonOperator.GT.toString)
//          .withAttributeValueList(new AttributeValue().withN("5"))
//        )
//    ).getItems
//    println(items)
//    items
//
//  }

  def createTable(name: String, hashKey: (String, ScalarAttributeType), rangeKey: (String, ScalarAttributeType)) = {
    ddb.createTable(new CreateTableRequest(
      name,
      new KeySchema()
        .withHashKeyElement(
          new KeySchemaElement()
            .withAttributeName(hashKey._1)
            .withAttributeType(hashKey._2)
         )
        .withRangeKeyElement(
        new KeySchemaElement()
          .withAttributeName(rangeKey._1)
          .withAttributeType(rangeKey._2)
        )
    ))
    new Table[Long](ddb, name, hashKey._1, rangeKey._1) with AttributeValueLongBijection
  }

  def getTable(name: String) = {
    val schema = ddb.describeTable(
      new DescribeTableRequest().withTableName(name)
    ).getTable.getKeySchema

    new Table[Long](ddb, name, schema.getHashKeyElement.getAttributeName, schema.getRangeKeyElement.getAttributeName) with AttributeValueLongBijection

  }


}

object DynamoDB {
  def create(credentialsFile: File): DynamoDB = {
    val ddbClient = new AmazonDynamoDBClient(new PropertiesCredentials(credentialsFile))
    ddbClient.setEndpoint("http://dynamodb.eu-west-1.amazonaws.com")
    new DynamoDB(ddbClient)
  }
}
