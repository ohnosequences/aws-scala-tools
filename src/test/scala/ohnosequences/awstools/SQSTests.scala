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
import scala.util.{ Try, Success, Failure, Random }


class SQSTests extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val sqsClient: AmazonSQSClient = sqs.client(
    new DefaultAWSCredentialsProviderChain(),
    Region.Ireland,
    PredefinedClientConfigurations.defaultConfig.withMaxConnections(100)
  )

  // we append a random suffix to avoid waiting 60 seconds between test runs
  val queueName: String = s"aws-scala-tools-sqs-testing-${Random.nextInt(100)}"
  lazy val queue: Queue = sqsClient.createOrGet(queueName).get

  override def beforeAll() = {
    queue.setVisibilityTimeout(2)
  }

  override def afterAll() = {
    queue.delete()
  }

  def queueInfo: String = s"${queue.approxMsgAvailable} available, ${queue.approxMsgInFlight} in flight"


  def testSendingInParallel(threads: Int, amount: Int, timeout: Duration) =
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
          inputs.map { msg => Future( queue.sendOne(msg).isSuccess ) }
        ) { _ && _ }
      }

      runWithTimer("in batches") {
        queue.sendBatch(inputs.toIterator).map { _.failures.isEmpty }
      }

      ec.shutdown()
    }

  // testSendingInParallel(16, 1000, 21 seconds)
  // testSendingInParallel(24, 1000, 21 seconds)
  // testSendingInParallel(32, 1000, 3 seconds)

  testSendingInParallel(32, 1000, 11 seconds)

  test("receiving and deleting a message") {

    val N = queue.approxMsgAvailable

    val result = queue.receiveOne.get

    result.foreach { msg =>
      info(msg.toString)
      msg.delete
    }
  }


  test("short-polling the queue") {

    info(queueInfo)

    val msgs = queue.poll(
      timeout = 10.seconds,
      amountLimit = None,
      adjustRequest = { _.withWaitTimeSeconds(2) }
    ).get
    info(s"polled: ${msgs.length}")

    info(queueInfo)
  }

  test("purging the queue") {
    // waiting for all the messages to return from flight
    Thread.sleep(3.seconds.toMillis)

    info(queueInfo)

    assert { queue.purge().isSuccess }

    Thread.sleep(1.seconds.toMillis)
    info(queueInfo)

    assert { queue.approxMsgAvailable == 0 }
    assert { queue.approxMsgInFlight == 0 }
  }
}
