package ohnosequences.awstools.test

import com.amazonaws.services.s3.AmazonS3
import ohnosequences.awstools._, s3._
import java.io.File
import java.nio.file._
import java.net.URI
import scala.util.Try
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

  test("S3 addresses are (de)constructed correctly") {

    val obj = s3"bucket" / "foo//bar" / "/buh"

    assertResult(Set("s3://bucket/foo/bar/buh")) {
      Set(
        obj.toURI.toString,
        obj.toString,
        obj.url
      )
    }

    assertResult("bucket") { obj.bucket }
    assertResult("foo/bar/buh") { obj.key }

    obj match {
      case S3Object(bucket, key) => {
        assertResult("bucket") { bucket }
        assertResult("foo/bar/buh") { key }
      }
    }

    val fldr = obj /

    assertResult(Set("s3://bucket/foo/bar/buh/")) {
      Set(
        fldr.toURI.toString,
        fldr.toString,
        fldr.url
      )
    }

    assertResult("bucket") { fldr.bucket }
    assertResult("foo/bar/buh/") { fldr.key }

    fldr match {
      case S3Folder(bucket, key) => {
        assertResult("bucket") { bucket }
        assertResult("foo/bar/buh/") { key }
      }
    }
  }

  test(s"Uploading to ${srcS3}") {
    val uploadTry = s3Client.upload(tmp.prefix, srcS3)
    assert { uploadTry.isSuccess }

    uploadTry.foreach { dst =>
      info(s"Uploaded [${tmp.prefix}] to [${srcS3}]")
    }
  }

  test(s"Copying one ${srcS3} to ${dstS3}") {
    val copyTry = s3Client.copy(srcS3, dstS3)
    assert { copyTry.isSuccess }

    copyTry.foreach { list =>
      list.foreach { obj =>
        info(s"Copied object ${obj.toString}")
      }
    }

    def suffixes(prefix: S3Folder, objs: List[S3Object]): List[URI] = objs.map { obj =>
      prefix.toURI.relativize(obj.toURI)
    }

    def listSuffixes(prefix: S3Folder): Try[List[URI]] =
      s3Client.listObjects(prefix).map { list =>
        suffixes(prefix, list)
      }

    assertResult(Set()) {
      listSuffixes(srcS3).get.toSet diff
      listSuffixes(dstS3).get.toSet
    }
  }

  test(s"Downloading from ${dstS3}") {
    val dst = Files.createTempDirectory(Paths.get("target"), "s3-testing").toFile

    val downloadTry = s3Client.download(dstS3, dst)
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
