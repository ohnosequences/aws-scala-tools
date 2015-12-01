
```scala
// package ohnosequences.awstools.sqs
//
// import com.amazonaws.services.sqs.AmazonSQS
// import ohnosequences.awstools.utils.SQSUtils
//
// import ohnosequences.awstools.test.awsClients
// import ohnosequences.benchmark.Bench
// import ohnosequences.logging.{ConsoleLogger, Logger}
// import org.junit.Test
// import org.junit.Assert._
//
// import java.io.File
//
// import scala.annotation.tailrec
// import scala.util.{Failure, Try, Success}
//
// class SQSTests {
//
//   @Test
//   def readAndWrite() {
//
//     val sqs: SQS = awsClients.sqs
//     val queue = sqs.createQueue("ohnosequences-queue-test")
//     val items = (1 to 100).map(_.toString).toList
//
//     val logger = new ConsoleLogger("sqs-tests", true)
//
//     val res =  logger.benchExecute("write messages") {
//       SQSUtils.writeBatch(sqs.sqs, queue.url, items)
//     }
//
//
//     assertEquals(Success(()), res)
//
//
//
//     val bench =  new Bench()
//     val logger2 = new ConsoleLogger("sqs-tests", false)
//     val r = receiveAll(sqs.sqs, queue.url, items.toSet, logger2, bench, System.currentTimeMillis() + 150 * 1000)
//     queue.delete()
//
//     println("messages received: " + bench.averageTime("receive") + " ms per message")
//
//     assertEquals(Success(()), r)
//   }
//
//   @tailrec
//   final def receiveAll(sqs: AmazonSQS, queueURL: String, toReceive: Set[String], logger: Logger, bench: Bench, timeThreshold: Long): Try[Unit] = {
//     if(toReceive.isEmpty) {
//       Success(())
//     } else if(System.currentTimeMillis() > timeThreshold) {
//       Failure(new Error("timeout"))
//     } else {
//       logger.benchExecute("receive", Some(bench)) {SQSUtils.receiveMessage(sqs, queueURL)} match {
//         case Failure(f) => Failure(f)
//         case Success(message) => {
//           logger.info(message.getBody + " received " + toReceive.size + " left")
//           SQSUtils.deleteMessage(sqs, queueURL, message.getReceiptHandle)
//           receiveAll(sqs, queueURL, toReceive.-( message.getBody), logger, bench, timeThreshold)
//         }
//       }
//     }
//   }
//
// }

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../main/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../../../../main/scala/ohnosequences/awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../../../../main/scala/ohnosequences/awstools/s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../../../../main/scala/ohnosequences/awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../../../../main/scala/ohnosequences/awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../../../main/scala/ohnosequences/benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../../../main/scala/ohnosequences/logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../../../main/scala/ohnosequences/logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: SQSTests.scala.md