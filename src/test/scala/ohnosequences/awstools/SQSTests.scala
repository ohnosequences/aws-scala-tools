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

      def runWithTimer(comment: String)(f: Future[Boolean]) = {
        val start = Deadline.now

        Try { Await.result(f, timeout) } match {
          case Failure(e) => fail(e.toString)
          case Success(result) => {

            val took = -start.timeLeft
            info(s"${comment}: ~${took.toMillis}ms")

            assert { result }
          }
        }
      }

      val inputs = (1 to amount).map(_.toString)

      runWithTimer("one by one") {
        Future.reduce(
          inputs.map { msg => Future( queue.send(msg).isSuccess ) }
        ) { _ && _ }
      }

      runWithTimer("in batches") {
        queue.sendBatch(inputs.toIterator).map { _.failures.isEmpty }
      }

      ec.shutdown()
    }

  // testInParallel(16, 1000, 21 seconds)
  // testInParallel(24, 1000, 21 seconds)
  testInParallel(32, 1000, 10 seconds)
}
