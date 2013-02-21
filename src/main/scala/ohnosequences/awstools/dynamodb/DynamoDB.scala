package ohnosequences.awstools.dynamodb

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model._

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper


abstract sealed class KeyType {
  type ValueType

  def toScalarAttributeType: ScalarAttributeType
}

object KeyType {

  def fromScalarAttributeType(scalarAttributeType: ScalarAttributeType) = scalarAttributeType match {
    case ScalarAttributeType.N => NumericType
    case ScalarAttributeType.S => StringType
    case _ => StringType
  }

  def fromAWSName(name: String) = name match {
    case "N" => NumericType
    case "S" => StringType
  }
}

case object NumericType extends KeyType {
  type ValueType = Long

  override def toScalarAttributeType = ScalarAttributeType.N
}

case object StringType extends KeyType {
  type ValueType = String

  override def toScalarAttributeType = ScalarAttributeType.S
}

abstract sealed class KeyValue[K <: KeyType] {
  def getValue: K#ValueType

  def getAttributeValue: AttributeValue
}

case class NumericValue(value: Long) extends KeyValue[NumericType.type] {
  override def getValue = value

  override def getAttributeValue = new AttributeValue().withN(value.toString)
}

case class StringValue(value: String) extends KeyValue[StringType.type] {
  override def getValue = value

  override def getAttributeValue = new AttributeValue().withS(value)
}

case class HashKey(name: String, keyType: KeyType)

case class RangeKey(name: String, keyType: KeyType)

case class Table(ddb: AmazonDynamoDBClient, name: String, hashKey: HashKey, rangeKey: RangeKey) {
  def waitForActivation() {
    //    ddb.describeTable(
    //      new DescribeTableRequest().withTableName(name)
    //    ).
    println("waiting for table")
    Thread.sleep(5000)
  }
}

class DynamoDB(val ddb: AmazonDynamoDBClient) {

  def shutdown() {
    ddb.shutdown()
  }

  def createMapper = DynamoObjectMapper(ddb, new DynamoDBMapper(ddb))

  def createTable(name: String, hashKey: HashKey, rangeKey: RangeKey, readUnits: Long = 1, writeUnits: Long = 1) = {
    ddb.createTable(new CreateTableRequest(
      name,
      new KeySchema()
        .withHashKeyElement(
        new KeySchemaElement()
          .withAttributeName(hashKey.name)
          .withAttributeType(hashKey.keyType.toScalarAttributeType)
      )
        .withRangeKeyElement(
        new KeySchemaElement()
          .withAttributeName(rangeKey.name)
          .withAttributeType(rangeKey.keyType.toScalarAttributeType)
      )
    ).withProvisionedThroughput(new ProvisionedThroughput()
      .withReadCapacityUnits(readUnits)
      .withWriteCapacityUnits(writeUnits)
    )
    )
    Table(ddb, name, hashKey, rangeKey)
  }

  def getTable(name: String): Option[Table] = {
    try {
      val schema = ddb.describeTable(
        new DescribeTableRequest().withTableName(name)
      ).getTable.getKeySchema
      val hashKeyElement = schema.getHashKeyElement
      val rangeKeyElement = schema.getRangeKeyElement

      val hashKey = HashKey(hashKeyElement.getAttributeName, KeyType.fromAWSName(hashKeyElement.getAttributeType))
      val rangeKey = RangeKey(rangeKeyElement.getAttributeName, KeyType.fromAWSName(rangeKeyElement.getAttributeType))

      Some(Table(ddb, name, hashKey, rangeKey))
    } catch {
      case e: ResourceNotFoundException => None
    }
  }
}

object DynamoDB {
  def create(credentialsFile: File): DynamoDB = {
    val ddbClient = new AmazonDynamoDBClient(new PropertiesCredentials(credentialsFile))
    ddbClient.setEndpoint("http://dynamodb.eu-west-1.amazonaws.com")
    new DynamoDB(ddbClient)
  }
}
