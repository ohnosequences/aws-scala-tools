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

