package ohnosequences.awstools.test.sqs

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry
import ohnosequences.awstools._, sqs._
import ohnosequences.awstools.regions._
import com.amazonaws.PredefinedClientConfigurations
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import java.util.concurrent.Executors
import scala.collection.JavaConversions._
import scala.concurrent._, duration._
import scala.util.{Failure, Try, Success}


class SQSTests extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val sqsClient: AmazonSQSClient = sqs.client(
    new ProfileCredentialsProvider("default"),
    PredefinedClientConfigurations.defaultConfig.withMaxConnections(100),
    Region.Ireland
  )

  lazy val queue: Queue = sqsClient.createOrGet("aws-scala-tools-sqs-testing").getOrElse(
    sys.error("Couldn't create or get the testing queue")
  )

  override def beforeAll() = {
    queue.purge()
  }


  def sendInParallel(amount: Int)(implicit ec: ExecutionContext): Future[Seq[Try[MessageId]]] =
    Future.sequence {
      (1 to amount).map { ix =>
        Future( queue.send(ix.toString) )
      }
    }

  // def sendBatchInParallel(amount: Int)(implicit ec: ExecutionContext): Future[Seq[Try[Any]]] =
  //   Future.sequence {
  //     (1 to amount).grouped(10).toSeq.map { group =>
  //       Future {
  //         Try {
  //           sqsClient.sendMessageBatch(queue.url.toString,
  //             group.map { ix =>
  //               new SendMessageBatchRequestEntry(ix.toString, ix.toString)
  //             }
  //           )
  //         }
  //       }
  //     }
  //   }


  def runTest(threads: Int, amount: Int, timeout: Duration) =
    test(s"sending messages in parallel: ${threads} threads, ${amount} messages") {
      queue.purge()

      implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threads))

      val start = Deadline.now

      Try { Await.result(sendInParallel(amount), timeout) } match {
        case Failure(e) => fail(e.toString)
        case Success(result) => {

          val took = -start.timeLeft
          info(s"took ~${took.toMillis}ms")

          assert { result.find(_.isFailure).isEmpty }
        }
      }

      ec.shutdown()
    }

  runTest(16, 1000, 11 seconds)
  runTest(24, 1000, 11 seconds)
  runTest(32, 1000, 11 seconds)
  runTest(64, 1000, 11 seconds)
}
