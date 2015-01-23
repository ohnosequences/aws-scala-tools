
```scala
package ohnosequences.awstools.dynamodb

import com.amazonaws.AmazonClientException
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model._
import ohnosequences.logging.Logger
import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}


object Utils {
  //when it should fail when repeat???

  object tester {
    var current10s = (System.currentTimeMillis() / 1000) / 10
    var counts = 0

    var prev: Int = 0

    def put(amount: Int): Unit = {


      val c = (System.currentTimeMillis() / 1000) / 10

      if(c == current10s) {
        counts += (amount - prev)
      }
      if(c > current10s) {
        current10s = c

        println("average speed: " + (10.0 / math.max(0.001, counts)))
        counts = 0
      }

      prev = amount
    }


  }

  def writeWriteRequests(ddb: AmazonDynamoDB, table: String, requests: List[WriteRequest], logger: Logger): Try[Unit] =
    writeWriteRequests0(ddb, table, requests, logger, System.currentTimeMillis(), requests.size)

  @tailrec
  def writeWriteRequests0(ddb: AmazonDynamoDB, table: String, requests: List[WriteRequest], logger: Logger, start: Long, init: Int): Try[Unit] = {
    if(requests == null || requests.isEmpty) {
      Success(())
    } else {
      //Try {
      val (left, right) = requests.splitAt(25)
      val javaLeftList: java.util.List[WriteRequest] = left


      val res: Try[Option[BatchWriteItemResult]] = try {
        Success(Some(ddb.batchWriteItem(Map(table -> javaLeftList))))
      } catch {
        case p: ProvisionedThroughputExceededException => //non fatal; retry
          logger.warn(p)
          //
          Success(None)
        case amazon: AmazonClientException => Failure(amazon)//report
      }

      res match {
        case Success(None) => {
          //repeat
          writeWriteRequests0(ddb, table, requests, logger, start, init)
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

        //  logger.info("unprocessed " + newLeft.size + " left " + right.size
        //    + " speed " + (System.currentTimeMillis() - start) / math.max(0.0001, init - requests.size - newLeft.size))
          tester.put((init - requests.size - newLeft.size))
          writeWriteRequests0(ddb, table, newLeft ++ right, logger, start, init)
        }
        case Failure(t) => Failure(t)
      }
    }
  }

  @tailrec
  def writeWriteRequestsB(ddb: AmazonDynamoDB, table: String, buffer: List[WriteRequest], requests: List[WriteRequest], logger: Logger, start: Long, init: Int): Try[Unit] = {
    if(buffer == null || buffer.isEmpty) {
      if(requests == null || requests.isEmpty) {
        Success(())
      } else {
        val (left, right) = requests.splitAt(25)
        writeWriteRequestsB(ddb, table, left, right, logger, start, init)
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
        case amazon: AmazonClientException => Failure(amazon)//report
      }

      res match {
        case Success(None) => {
          //repeat
          writeWriteRequestsB(ddb, table, buffer, requests, logger, start, init)
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
        //  logger.info("unprocessed " + newLeft.size + " left " + requests.size
         //   + " speed " + (System.currentTimeMillis() - start) / (init - requests.size - newLeft.size))
          tester.put((init - requests.size - newLeft.size))

          writeWriteRequestsB(ddb, table, newLeft, requests, logger, start, init)
        }
        case Failure(t) => Failure(t)
      }
    }
  }

  @tailrec
  def writeWriteRequestsNonBatch(ddb: AmazonDynamoDB, table: String, requests: List[WriteRequest], logger: Logger, startTime: Long, init: Int): Try[Unit] = {
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
           // logger.info("left " + tail.size + " speed " + (System.currentTimeMillis() - startTime) / (init - tail.size))
            tester.put((init - requests.size - 1))

            writeWriteRequestsNonBatch(ddb, table, tail, logger, startTime, init)

          }
          case Success(false) => writeWriteRequestsNonBatch(ddb, table, requests, logger, startTime, init)

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
```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: ../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: ../../../../../test/scala/ohnosequences/awstools/InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md