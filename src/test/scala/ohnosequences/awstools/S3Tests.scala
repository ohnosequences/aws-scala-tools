package ohnosequences.awstools.s3


import org.junit.Test
import org.junit.Assert._

import java.io.File
import ohnosequences.awstools.sqs.SQS

class S3Tests {

  @Test
  def objectsTests {
//    val s3 = S3.create(new File("AwsCredentials.properties"))
//
//    val objectAddress = ObjectAddress("awstools-test-bucket", "test-object")
//    val testBucket = s3.createBucket(objectAddress.bucket)
//
//    val testString = "тестовая строка"
//
//    s3.putWholeObject(objectAddress, testString)
//    val result = s3.readWholeObject(objectAddress)
//
//    assertEquals(testString, result)
//    s3.deleteObject(objectAddress)
//    testBucket.delete


  }


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

