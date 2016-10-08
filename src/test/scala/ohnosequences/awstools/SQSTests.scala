package ohnosequences.awstools.test.sqs

import com.amazonaws.services.sqs.AmazonSQSClient
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry
import ohnosequences.awstools._, sqs._
import ohnosequences.awstools.regions._
import com.amazonaws.PredefinedClientConfigurations
import com.amazonaws.auth._
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import java.util.concurrent.Executors
import scala.collection.JavaConversions._
import scala.concurrent._, duration._
import scala.util.{Failure, Try, Success}


class SQSTests extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val sqsClient: AmazonSQSClient = sqs.client(
    new DefaultAWSCredentialsProviderChain(),
    PredefinedClientConfigurations.defaultConfig.withMaxConnections(100),
    Region.Ireland
  )

  lazy val queue: Queue = sqsClient.createOrGet("aws-scala-tools-sqs-testing").getOrElse(
    sys.error("Couldn't create or get the testing queue")
  )

  override def beforeAll() = {
    queue.purge()
  }



  def testInParallel(threads: Int, amount: Int, timeout: Duration) =
    test(s"sending messages in parallel: ${threads} threads, ${amount} messages") {

      implicit val ec = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(threads))

      val inputs = (1 to amount).map(_.toString)

      def sendOneByOne = Future.reduce(
        inputs.map { msg => Future( queue.send(msg).isSuccess ) }
      ) { _ && _ }

      def sendInBatches = queue.sendBatch(inputs.toIterator).map { _.failures.isEmpty }

      def runWithTimer(f: Future[Boolean], comment: String) = {
        val start = Deadline.now

        Try { Await.result(f, timeout) } match {
          case Failure(e) => fail(e.toString)
          case Success(result) => {

            val took = -start.timeLeft
            info(s"took ~${took.toMillis}ms ${comment}")

            assert { result }
          }
        }
      }

      runWithTimer(sendOneByOne, "sending one by one")
      runWithTimer(sendInBatches, "sending in batches")

      ec.shutdown()
    }

  // testInParallel(16, 1000, 21 seconds)
  // testInParallel(24, 1000, 21 seconds)
  testInParallel(32, 1000, 10 seconds)
}
