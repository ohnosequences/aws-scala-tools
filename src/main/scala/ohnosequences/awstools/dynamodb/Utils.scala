package ohnosequences.awstools.dynamodb

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model._
import ohnosequences.logging.Logger
import scala.collection.JavaConversions._


object Utils {

  def deleteTable(ddb: AmazonDynamoDB, table: String) {
    try {
      ddb.deleteTable(new DeleteTableRequest().withTableName(table))
    } catch {
      case t: Throwable => println("can't delete table " + table); t.printStackTrace()
    }
  }

  def waitForResource[A](resourceCheck: => Option[A]) : Option[A] = {
    var iteration = 1
    var current: Option[A] = None
    val limit = 100

    do {
      current = resourceCheck
      iteration += 1
      Thread.sleep(5000)
    } while (current.isEmpty && iteration < limit)

    current
  }

  def changeThroughput(ddb: AmazonDynamoDB,
                       table: String,
                       writeThroughput: Int = 1,
                       readThroughput: Int = 1
                        ) {
    ddb.updateTable(new UpdateTableRequest()
      .withTableName(table)
      .withProvisionedThroughput(new ProvisionedThroughput(readThroughput, writeThroughput))
    )
  }


  def createTable(ddb: AmazonDynamoDB,
                  tableName: String,
                  hash: AttributeDefinition,
                  range: Option[AttributeDefinition] = None,
                  logger: Logger,
                  writeThroughput: Int = 1,
                  readThroughput: Int = 1,
                  waitForCreation: Boolean = true
                   ): Boolean =  {
    try {
      ddb.describeTable(new DescribeTableRequest()
        .withTableName(tableName)
      )
      logger.warn("warning: table " + tableName + " already exists")
      true
    } catch {
      case e: ResourceNotFoundException => {

        //create table
        var request = new CreateTableRequest()
          .withTableName(tableName)
          .withProvisionedThroughput(new ProvisionedThroughput(readThroughput, writeThroughput))

        range match {
          case Some(rng) => {
            request = request
              .withKeySchema(
                new KeySchemaElement(hash.getAttributeName, "HASH"),
                new KeySchemaElement(rng.getAttributeName, "RANGE")
              )
              .withAttributeDefinitions(
                new AttributeDefinition(hash.getAttributeName, hash.getAttributeType),
                new AttributeDefinition(rng.getAttributeName, rng.getAttributeType)
              )
          }
          case None => {
            request = request
              .withKeySchema(
                new KeySchemaElement(hash.getAttributeName, "HASH")
              )
              .withAttributeDefinitions(
                new AttributeDefinition(hash.getAttributeName, hash.getAttributeType)
              )
          }
        }

        ddb.createTable(request)

        if (waitForCreation) {
          !Utils.waitForResource {
            logger.info("waiting for table " + tableName)
            getTableState(ddb, tableName).flatMap{
              case "ACTIVE" => Some("ACTIVE")
              case _ => None
            }
          }.isEmpty
        } else {
          true
        }
      }
    }
  }

  def getTableState(ddb: AmazonDynamoDB, name: String): Option[String] = {
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