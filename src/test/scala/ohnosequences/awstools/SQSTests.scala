package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.AmazonSQS
import ohnosequences.awstools.utils.SQSUtils

import ohnosequences.awstools.TestCredentials
import ohnosequences.benchmark.Bench
import ohnosequences.logging.{ConsoleLogger, Logger}
import org.junit.Test
import org.junit.Assert._

import java.io.File

import scala.annotation.tailrec
import scala.util.{Failure, Try, Success}

class SQSTests {

  @Test
  def readAndWrite() {

    TestCredentials.aws match {
      case None => {
        println("this test requires test aws credentials")
      }
      case Some(awsClients) => {
        val sqs: SQS = awsClients.sqs
        val queue = sqs.createQueue("ohnosequences-queue-test")
        val items = (1 to 100).map(_.toString).toList

        val logger = new ConsoleLogger("sqs-tests", true)

        val res =  logger.benchExecute("write messages") {
          SQSUtils.writeBatch(sqs.sqs, queue.url, items)
        }


        assertEquals(Success(()), res)



        val bench =  new Bench()
        val logger2 = new ConsoleLogger("sqs-tests", false)
        val r = receiveAll(sqs.sqs, queue.url, items.toSet, logger2, bench, System.currentTimeMillis() + 150 * 1000)
        queue.delete()

        println("messages received: " + bench.averageTime("receive") + " ms per message")

        assertEquals(Success(()), r)

      }
  }

  @tailrec
  def receiveAll(sqs: AmazonSQS, queueURL: String, toReceive: Set[String], logger: Logger, bench: Bench, timeThreshold: Long): Try[Unit] = {
    if(toReceive.isEmpty) {
      Success(())
    } else if(System.currentTimeMillis() > timeThreshold) {
      Failure(new Error("timeout"))
    } else {
      logger.benchExecute("receive", Some(bench)) {SQSUtils.receiveMessage(sqs, queueURL)} match {
        case Failure(f) => Failure(f)
        case Success(message) => {
          logger.info(message.getBody + " received " + toReceive.size + " left")
          SQSUtils.deleteMessage(sqs, queueURL, message.getReceiptHandle)
          receiveAll(sqs, queueURL, toReceive.-( message.getBody), logger, bench, timeThreshold)
        }
      }
    }
  }
  }

}

