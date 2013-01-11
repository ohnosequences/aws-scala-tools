package awsaws.sqs

import org.junit.Test
import org.junit.Assert._

import java.io.File

class SQSTests {

  @Test
  def policyTests {

    val sqs = SQS.create(new File("AwsCredentials.properties"))
    val queueName = "test_" + System.currentTimeMillis
    val queue = sqs.createQueue(queueName)

    try {
      assertEquals(queue.url, sqs.getQueueByName(queueName).get.url)
      assertEquals(None, sqs.getQueueByName("unexisting queue"))
    } finally {
      queue.delete()
      sqs.shutdown()
    }
  }
}

