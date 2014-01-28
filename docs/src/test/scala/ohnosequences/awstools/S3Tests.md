### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../../../../main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../../../../main/scala/ohnosequences/awstools/ec2/EC2.md)
            + [Filters.scala](../../../../main/scala/ohnosequences/awstools/ec2/Filters.md)
            + [InstanceType.scala](../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.md)
            + [Utils.scala](../../../../main/scala/ohnosequences/awstools/ec2/Utils.md)
          + regions
            + [Region.scala](../../../../main/scala/ohnosequences/awstools/regions/Region.md)
          + s3
            + [Bucket.scala](../../../../main/scala/ohnosequences/awstools/s3/Bucket.md)
            + [S3.scala](../../../../main/scala/ohnosequences/awstools/s3/S3.md)
          + sns
            + [SNS.scala](../../../../main/scala/ohnosequences/awstools/sns/SNS.md)
            + [Topic.scala](../../../../main/scala/ohnosequences/awstools/sns/Topic.md)
          + sqs
            + [Queue.scala](../../../../main/scala/ohnosequences/awstools/sqs/Queue.md)
            + [SQS.scala](../../../../main/scala/ohnosequences/awstools/sqs/SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](DynamoDBTests.md)
          + [EC2Tests.scala](EC2Tests.md)
          + [S3Tests.scala](S3Tests.md)
          + [SNSTests.scala](SNSTests.md)
          + [SQSTests.scala](SQSTests.md)

------


```scala
package ohnosequences.awstools.s3


import org.junit.Test
import org.junit.Assert._

import java.io.File
import ohnosequences.awstools.s3._
import com.amazonaws.services.s3.transfer.TransferManager

class S3Tests {

  @Test
  def objectsTests {

  }

  @Test
  def multiPartTest {
//    val s3 = S3.create(new File("AwsCredentials.properties"))
//    val bucket = "awstools-test-bucket"
//    val file = new File("build.sbt")
//    val objectAddress = ObjectAddress(bucket, file.getName)
//    s3.createBucket(bucket)
//
//    val loadManager = s3.createLoadingManager()
//    loadManager.upload(objectAddress, file)
//
//    val tmpFile = File.createTempFile("test", "file")
//    println("created temp file: " + tmpFile.getAbsolutePath)
//    loadManager.download(objectAddress, tmpFile)
//    tmpFile.delete()
//    s3.deleteBucket(bucket)
//
//    val content1 = scala.io.Source.fromFile(file).mkString
//    val content2 = scala.io.Source.fromFile(file).mkString
//    assertEquals(content1, content2)

    //val transferManager = new TransferManager(s3.s3)
//    val upload = transferManager.upload(bucket, "test", new File("build.sbt"))
//
//
//    while(!upload.isDone)   {
//      println("Transfer: " + upload.getDescription())
//      println("  - State: " + upload.getState())
//      println("  - Progress: " +   upload.getProgress().getBytesTransfered())
//      // Do work while we wait for our upload to complete...
//      Thread.sleep(500)
//    }
//    transferManager.shutdownNow()



  }



}


```

