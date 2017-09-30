package ohnosequences.awstools.test

import com.amazonaws.services.s3.AmazonS3
import ohnosequences.awstools._, s3._
// import scala.util.{ Try, Success, Failure, Random }
import java.io.File
import java.nio.file._
import scala.collection.JavaConverters._

case class tmpFiles(prefix: File) {
  val f1 = new File(prefix, "f1")
  val f2 = new File(prefix, "foo/f2")

  val files = List(f1, f2)

  def write(file: File) = {
    val parent = file.getParentFile
    if (!parent.exists) Files.createDirectories(parent.toPath)
    Files.write(file.toPath, List(file.getName).asJava)
  }

  def writeFiles() = files.foreach(write)
}

class S3 extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val s3Client: AmazonS3 = s3.defaultClient

  lazy val s3prefix = s3"aws-scala-tools-testing" / "s3" /

  lazy val srcS3 = s3prefix / "test1" /
  lazy val dstS3 = s3prefix / "test2" /

  lazy val tmp = tmpFiles(
    Files.createTempDirectory(Paths.get("target"), "s3-testing").toFile
  )

  override def beforeAll() = {
    if (!s3Client.doesBucketExistV2(s3prefix.bucket))
      s3Client.createBucket(s3prefix.bucket)

    tmp.writeFiles()
  }

  override def afterAll() = {
    // s3Client.deleteBucket(s3prefix.bucket)
    s3Client.listObjects(s3prefix).foreach { list =>
      list.foreach { obj =>
        s3Client.deleteObject(obj.bucket, obj.key)
      }
    }
  }


  test(s"Uploading to ${srcS3}") {
    val uploadTry = s3Client.transfer { _.upload(tmp.prefix, srcS3) }
    assert { uploadTry.isSuccess }

    uploadTry.foreach { dst =>
      info(s"Uploaded [${tmp.prefix}] to [${srcS3}]")
    }
  }

  test(s"Copying one ${srcS3} to ${dstS3}") {
    val copyTry = s3Client.transfer { _.copy(srcS3, dstS3) }
    assert { copyTry.isSuccess }

    copyTry.foreach { list =>
      list.foreach { obj =>
        info(s"Copied object ${obj.toString}")
      }
    }
  }

  test(s"Downloading from ${dstS3}") {
    val dst = Files.createTempDirectory(Paths.get("target"), "s3-testing").toFile

    val downloadTry = s3Client.transfer { _.download(dstS3, dst) }
    assert { downloadTry.isSuccess }

    downloadTry.foreach { file =>
      info(s"Downloaded [${dstS3}] to [${file}]")

      val dstTmp = tmpFiles(file)

      assert { dstTmp.f1.exists }
      assert { dstTmp.f2.exists }

      // Checking that the content if the same as we uploaded:
      assert {
        Files.readAllLines(tmp.f1.toPath) ==
        Files.readAllLines(dstTmp.f1.toPath)
      }
      assert {
        Files.readAllLines(tmp.f2.toPath) ==
        Files.readAllLines(dstTmp.f2.toPath)
      }
    }
  }
}
