package ohnosequences.awstools.s3

import org.junit.Test
import org.junit.Assert._

import ohnosequences.awstools.TestCredentials
import java.io.File
import ohnosequences.awstools.s3._
import com.amazonaws.services.s3.transfer.TransferManager

class S3Tests {


  @Test
  def loadingManager {
    TestCredentials.aws match {
      case None => {
        println("this test requires test aws credentials")
      }
      case Some(awsClients) => {
        val s3: S3 = awsClients.s3

        val bucket = "ohnosequences-awstools-test"

        s3.createBucket(bucket)
        val file = new File("build.sbt")
        val objectAddress = ObjectAddress(bucket, file.getName)


        val loadManager = s3.createLoadingManager()
        loadManager.upload(objectAddress, file)

        val tmpFile = File.createTempFile("test", "file")
        println("created temp file: " + tmpFile.getAbsolutePath)
        loadManager.download(objectAddress, tmpFile)

        val content1 = scala.io.Source.fromFile(file).mkString
        val content2 = scala.io.Source.fromFile(tmpFile).mkString

        assertEquals(content1, content2)

        tmpFile.delete()


        s3.deleteBucket(bucket)

      }

    }






  }



}

