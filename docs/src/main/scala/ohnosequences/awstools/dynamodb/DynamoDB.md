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
          + regions
            + [Region.scala](../regions/Region.md)
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

import java.io.File

import ohnosequences.awstools.regions.Region._

import com.amazonaws.auth._
import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.services.dynamodb.model._

import com.amazonaws.services.dynamodb.datamodeling.DynamoDBMapper
import scala.collection.JavaConversions._
import com.amazonaws.internal.StaticCredentialsProvider


abstract sealed class KeyType {
  type ValueType

  def toScalarAttributeType: ScalarAttributeType

  def constructValue(s: String): AttributeValue
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

  override def constructValue(s: String) = new AttributeValue().withN(s)
}

case object StringType extends KeyType {
  type ValueType = String

  override def toScalarAttributeType = ScalarAttributeType.S
  override def constructValue(s: String) = new AttributeValue().withS(s)
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

  //todo add ytpes here!
  def putItem(hash: String, range: String, map: Map[String, String]) {


    val keys = Map[String, AttributeValue] (
      hashKey.name -> hashKey.keyType.constructValue(hash),
      rangeKey.name -> rangeKey.keyType.constructValue(hash)
    )

    ddb.putItem(new PutItemRequest()
      .withTableName(name)
      .withItem(map.mapValues(StringValue(_).getAttributeValue) ++ keys)
    )
  }

  def getItem(hash: String, range: String): Option[Map[String, String]] = {

    val key = new Key(hashKey.keyType.constructValue(hash), rangeKey.keyType.constructValue(range))
    val item = ddb.getItem(new GetItemRequest()
      .withTableName(name)
      .withKey(key)
    )

    if (item == null || item.getItem == null) {
      None

    } else {
      Some(item.getItem.mapValues(_.getS).toMap)
    }
  }

  def incrementCounter(counterName: String, hashKeyValue: String, rangeKeyValue: String) = {
    ddb.updateItem(new UpdateItemRequest()
      .withTableName(name)
      .withKey(new Key()
      .withHashKeyElement(hashKey.keyType.constructValue(hashKeyValue))
      .withRangeKeyElement(rangeKey.keyType.constructValue(rangeKeyValue))
    ).withAttributeUpdates(Map(
      counterName -> new AttributeValueUpdate()
        .withValue(new AttributeValue().withN("1"))
        .withAction(AttributeAction.ADD)
    )).withReturnValues(ReturnValue.ALL_NEW)
    ).getAttributes.get(counterName).getN
  }
}

class DynamoDB(val ddb: AmazonDynamoDBClient) {

  def shutdown() {
    ddb.shutdown()
  }



  def createMapper = DynamoObjectMapper(ddb, new DynamoDBMapper(ddb))

  def createTable(name: String, hashKey: HashKey, rangeKey: RangeKey, waitForActivation: Boolean = true, readUnits: Long = 1, writeUnits: Long = 1): Table = {
    getTable(name) match {
      case Some(table) => table
      case None => {
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
        if (waitForActivation) {
          waitForTable(name)
        }
        Table(ddb, name, hashKey, rangeKey)
      }
      }
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

  def getTableStatus(name: String): String = {
    ddb.describeTable(new DescribeTableRequest()
      .withTableName(name)
    ).getTable.getTableStatus
  }

  def waitForTable(name: String) {
    println("waiting for table")
    val TIMEOUT = 5000
    var ready = false

    var i = 0
    while(!ready) {
      print(".")
      Thread.sleep(TIMEOUT)
      if (getTableStatus(name).equals("ACTIVE") || i > 100) {
        ready = true
      }
      i = i + 1
    }
    println("")
  }

  def deleteTable(name: String) {
    try {
    ddb.deleteTable(new DeleteTableRequest()
      .withTableName(name)
    )
    } catch {
      case e:ResourceNotFoundException => ;
    }
  }

}

object DynamoDB {

  def create(): DynamoDB = {
    create(new InstanceProfileCredentialsProvider())
  }

  def create(credentialsFile: File): DynamoDB = {
    create(new StaticCredentialsProvider(new PropertiesCredentials(credentialsFile)))
  }

  def create(accessKey: String, secretKey: String): DynamoDB = {
    create(new StaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
  }

  def create(credentials: AWSCredentialsProvider): DynamoDB = {

    val ddbClient = new AmazonDynamoDBClient(credentials)
    ddbClient.setRegion(Ireland)
    new DynamoDB(ddbClient)
  }
}

```

