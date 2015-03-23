
```scala
package ohnosequences.awstools.dynamodb

import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model._
import ohnosequences.logging.Logger
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}


object DynamoDBUtils {


  /**
   * writes request in batch mode, always tries to write maximum -- 25
   * @param ddb AWS DynamoDB client
   * @param table table name
   * @param requests list of request
   * @param logger
   */
  def writeWriteRequests(ddb: AmazonDynamoDB, table: String, requests: List[WriteRequest], logger: Logger): Try[Unit] = {


    @tailrec
    def writeWriteRequestsRec(requests: List[WriteRequest]): Try[Unit] = {

      if (requests == null || requests.isEmpty) {
        Success(())
      } else {
        val (left, right) = requests.splitAt(25)
        val javaLeftList: java.util.List[WriteRequest] = left


        val res: Try[Option[BatchWriteItemResult]] = try {
          Success(Some(ddb.batchWriteItem(Map(table -> javaLeftList))))
        } catch {
          case p: ProvisionedThroughputExceededException => //non fatal; retry
            logger.warn(p)
            //
            Success(None)
          case amazon: AmazonClientException => Failure(amazon) //report
        }

        res match {
          case Success(None) => {
            //repeat
            writeWriteRequestsRec(requests)
          }
          case Success(Some(r)) => {
            val newLeft: List[WriteRequest] = Option(r.getUnprocessedItems).map { mapOperations =>
              val unprocessedList = mapOperations.get(table)
              if (unprocessedList == null) {
                List[WriteRequest]()
              } else {
                unprocessedList.toList
              }
            }.getOrElse(left)

            writeWriteRequestsRec(newLeft ++ right)
          }
          case Failure(t) => Failure(t)
        }
      }
    }
    writeWriteRequestsRec(requests)
  }

  /**
   * writes request in batch mode
   * @param ddb AWS DynamoDB client
   * @param table table name
   * @param requests list of request
   * @param logger
   */
  def writeWriteRequestsBatchBuff(ddb: AmazonDynamoDB, table: String, buffer: List[WriteRequest], requests: List[WriteRequest], logger: Logger): Try[Unit] = {

    @tailrec
    def writeWriteRequestsBatchBuffRec(buffer: List[WriteRequest], requests: List[WriteRequest]): Try[Unit] = {
      if (buffer == null || buffer.isEmpty) {
        if (requests == null || requests.isEmpty) {
          Success(())
        } else {
          val (left, right) = requests.splitAt(25)
          writeWriteRequestsBatchBuffRec(left, right)
        }
      } else {
        //Try {

        val javaLeftList: java.util.List[WriteRequest] = buffer

        val res: Try[Option[BatchWriteItemResult]] = try {
          Success(Some(ddb.batchWriteItem(Map(table -> javaLeftList))))
        } catch {
          case p: ProvisionedThroughputExceededException => //non fatal; retry
            logger.warn(p)
            //
            Success(None)
          case amazon: AmazonClientException => Failure(amazon) //report
        }

        res match {
          case Success(None) => {
            //repeat
            writeWriteRequestsBatchBuffRec(buffer, requests)
          }
          case Success(Some(r)) => {
            val newLeft: List[WriteRequest] = Option(r.getUnprocessedItems).map { mapOperations =>
              val unprocessedList = mapOperations.get(table)
              if (unprocessedList == null) {
                List[WriteRequest]()
              } else {
                unprocessedList.toList
              }
            }.getOrElse(buffer)
            writeWriteRequestsBatchBuffRec(newLeft, requests)
          }
          case Failure(t) => Failure(t)
        }
      }
    }

    writeWriteRequestsBatchBuffRec(List[WriteRequest](), requests)
  }

  @tailrec
  def writeWriteRequestsNonBatch(ddb: AmazonDynamoDB, table: String, requests: List[WriteRequest], logger: Logger): Try[Unit] = {
    requests match {
      case null => Success(())
      case Nil => Success(())
      case head :: tail => {

        val itemWritten = try {
          ddb.putItem(table, head.getPutRequest.getItem)
          Success(true)
        } catch {
          case p: ProvisionedThroughputExceededException => {
            //non fatal
            Success(false)
          }
          case a: AmazonClientException => Failure(a)
        }

        itemWritten match {
          case Failure(f) => Failure(f)
          case Success(true) => {
            writeWriteRequestsNonBatch(ddb, table, tail, logger)
          }
          case Success(false) => writeWriteRequestsNonBatch(ddb, table, requests, logger)
        }
      }
    }
  }



  def deleteTable(ddb: AmazonDynamoDB, table: String) {
    try {
      ddb.deleteTable(new DeleteTableRequest().withTableName(table))
    } catch {
      case t: Throwable => println("can't delete table " + table); t.printStackTrace()
    }
  }


  def waitForResource[A](resourceCheck: => Option[A], iterationsThreshold: Int = 100) : Option[A] = {

    @tailrec
    def waitForResourceRec(iteration: Int): Option[A] = {
      if (iteration > iterationsThreshold) {
        None
      } else {
        val current = resourceCheck
        resourceCheck match {
          case None => {
            Thread.sleep(5000)
            waitForResourceRec(iteration + 1)
          }
          case Some(res) => {
            Some(res)
          }
        }
      }
    }

    waitForResourceRec(1)

  }

  def changeThroughput(ddb: AmazonDynamoDB,
                       table: String,
                       readThroughput: Long = 1,
                       writeThroughput: Long = 1
                        ) {
    ddb.updateTable(new UpdateTableRequest()
      .withTableName(table)
      .withProvisionedThroughput(new ProvisionedThroughput()
        .withReadCapacityUnits(readThroughput)
        .withWriteCapacityUnits(writeThroughput)
      )
    )
  }


  def createTable(ddb: AmazonDynamoDB,
                  tableName: String,
                  hash: AttributeDefinition,
                  range: Option[AttributeDefinition] = None,
                  logger: Logger,
                  writeThroughput: Long = 1,
                  readThroughput: Long = 1,
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
          .withProvisionedThroughput(new ProvisionedThroughput()
            .withReadCapacityUnits(readThroughput)
            .withWriteCapacityUnits(writeThroughput)
          )

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
          waitForResource {
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


  //with repeats
  def getItem(ddb: AmazonDynamoDB,
              tableName: String,
              key: Map[String, AttributeValue],
              attributesToGet: Seq[String],
              logger: Logger): Try[Map[String, AttributeValue]] = {

    @tailrec
    def getItemRec(): Try[Map[String, AttributeValue]] = {
      try {
        val rawItem = ddb.getItem(new GetItemRequest()
          .withTableName(tableName)
          .withKey(key)
          .withAttributesToGet(attributesToGet)
        ).getItem
        if (rawItem != null) {
          Success(rawItem.toMap)
        } else {
          Failure(new NullPointerException)
        }
      } catch {
        case p: ProvisionedThroughputExceededException => {
          getItemRec()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    getItemRec()
  }

  //with repeats
  def deleteItem(ddb: AmazonDynamoDB,
                 tableName: String,
                 key: Map[String, AttributeValue],
                 logger: Logger): Try[Unit] = {

    @tailrec
    def deleteItemRep(): Try[Unit] = {
      try {
        val rawItem = ddb.deleteItem(new DeleteItemRequest()
          .withTableName(tableName)
          .withKey(key)
        )
        Success(())
      } catch {
        case p: ProvisionedThroughputExceededException => {
          deleteItemRep()
        }
        case a: AmazonClientException => {
          Failure(a)
        }
      }
    }

    deleteItemRep()
  }



}
```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]
            + [AutoScalingGroup.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]
          + [AWSClients.scala][main\scala\ohnosequences\awstools\AWSClients.scala]
          + dynamodb
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]
          + ec2
            + [EC2.scala][main\scala\ohnosequences\awstools\ec2\EC2.scala]
            + [Filters.scala][main\scala\ohnosequences\awstools\ec2\Filters.scala]
            + [InstanceType.scala][main\scala\ohnosequences\awstools\ec2\InstanceType.scala]
            + [Utils.scala][main\scala\ohnosequences\awstools\ec2\Utils.scala]
          + regions
            + [Region.scala][main\scala\ohnosequences\awstools\regions\Region.scala]
          + s3
            + [Bucket.scala][main\scala\ohnosequences\awstools\s3\Bucket.scala]
            + [S3.scala][main\scala\ohnosequences\awstools\s3\S3.scala]
          + sns
            + [SNS.scala][main\scala\ohnosequences\awstools\sns\SNS.scala]
            + [Topic.scala][main\scala\ohnosequences\awstools\sns\Topic.scala]
          + sqs
            + [Queue.scala][main\scala\ohnosequences\awstools\sqs\Queue.scala]
            + [SQS.scala][main\scala\ohnosequences\awstools\sqs\SQS.scala]
          + utils
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]
            + [SQSUtils.scala][main\scala\ohnosequences\awstools\utils\SQSUtils.scala]
        + benchmark
          + [Benchmark.scala][main\scala\ohnosequences\benchmark\Benchmark.scala]
        + logging
          + [Logger.scala][main\scala\ohnosequences\logging\Logger.scala]
          + [S3Logger.scala][main\scala\ohnosequences\logging\S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [EC2Tests.scala][test\scala\ohnosequences\awstools\EC2Tests.scala]
          + [InstanceTypeTests.scala][test\scala\ohnosequences\awstools\InstanceTypeTests.scala]
          + [RegionTests.scala][test\scala\ohnosequences\awstools\RegionTests.scala]
          + [S3Tests.scala][test\scala\ohnosequences\awstools\S3Tests.scala]
          + [SQSTests.scala][test\scala\ohnosequences\awstools\SQSTests.scala]
          + [TestCredentials.scala][test\scala\ohnosequences\awstools\TestCredentials.scala]

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: ..\sns\SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: ..\sns\Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md