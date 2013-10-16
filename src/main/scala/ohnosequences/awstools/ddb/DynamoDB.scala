package ohnosequences.awstools.ddb

import com.amazonaws.auth.{AWSCredentialsProvider, BasicAWSCredentials, PropertiesCredentials, InstanceProfileCredentialsProvider}
import java.io.File
import com.amazonaws.internal.StaticCredentialsProvider
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClient}
import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.model._

import scala.collection.JavaConversions._
import ohnosequences.awstools.utils.Utils
import org.slf4j.Logger
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper


class DynamoDB(val ddb: AmazonDynamoDB) {

  //todo create it again in case of failure

//  .withLocalSecondaryIndexes(new LocalSecondaryIndex()
//    .withKeySchema(new KeySchemaElement("length", "HASH"))
//  )


  def createTable[AS <: Attributes](name: String, attributes: AS, logger: Option[Logger] = None) = {
    try {

      val hash = new AttributeDefinition("hash", ScalarAttributeType.N)
      val schema = new KeySchemaElement(hash.getAttributeName, "HASH") :: attributes.getKeySchema
      val definitions = hash :: attributes.getKeyDefinitions

      ddb.createTable(new CreateTableRequest()
        .withTableName(name)
        .withAttributeDefinitions(definitions)
        .withProvisionedThroughput(new ProvisionedThroughput(1, 1))
        .withKeySchema(schema)
//        .withLocalSecondaryIndexes(new LocalSecondaryIndex()
//          .withProjection(new Projection()
//            .withProjectionType(ProjectionType.ALL)
//          )
//          .withIndexName("length")
//          .withKeySchema(
//            new KeySchemaElement("idC", "HASH"),
//            new KeySchemaElement("length", "RANGE")
//          )
//        )
      )
    } catch {
      case r: ResourceInUseException => ()
    }
    // new AttributeDefinition("content", ScalarAttributeType.S)

  //fix race conditions!
    Utils.waitForResource {
      logger.foreach(_.info("waiting for table"))
      getTableState(name).flatMap{
        case "ACTIVE" => Some("ACTIVE")
        case _ => None
      }
    }

    new Table(DynamoDB.this, name, attributes)

  }



//  def createTable[AS <: Attributes](name: String, attributes: AS, logger: Option[Logger] = None, workersCount: Int) = {
//    try {
//
//      val hash = new AttributeDefinition("hash", ScalarAttributeType.N)
//      val schema = new KeySchemaElement(hash.getAttributeName, "HASH") :: attributes.getKeySchema
//      val definitions = hash :: attributes.getKeyDefinitions
//
//      ddb.createTable(new CreateTableRequest()
//        .withTableName(name)
//        .withAttributeDefinitions(definitions)
//        .withProvisionedThroughput(new ProvisionedThroughput(1, 1))
//        .withKeySchema(schema)
//      )
//    } catch {
//      case r: ResourceInUseException => ()
//    }
//    // new AttributeDefinition("content", ScalarAttributeType.S)
//
//    //fix race conditions!
//    Utils.waitForResource {
//      logger.foreach(_.info("waiting for table"))
//      getTableState(name).flatMap{
//        case "ACTIVE" => Some("ACTIVE")
//        case _ => None
//      }
//    }
//
//    new Table(DynamoDB.this, name, attributes)
//
//  }



  def createMapper(): DynamoObjectMapper = {
    new DynamoObjectMapper(new DynamoDBMapper(ddb))
  }

  def getTableState(name: String): Option[String] = {
    try {
      val r = ddb.describeTable(new DescribeTableRequest()
        .withTableName(name)
      ).getTable.getTableStatus
      Some(r)
    } catch {
      case r: ResourceNotFoundException => None
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
    ddbClient.setRegion(com.amazonaws.regions.Region.getRegion(Regions.EU_WEST_1))
    new DynamoDB(ddbClient)
  }
}
