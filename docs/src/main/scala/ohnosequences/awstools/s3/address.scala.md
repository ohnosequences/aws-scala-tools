
```scala
package ohnosequences.awstools.s3

import ohnosequences.awstools.regions._

import java.net.URI
import java.net.URL


sealed trait AnyS3Address {
  // These are the inputs
  val _bucket: String
  val _key: String

  def toURI: URI = new URI("s3", _bucket, s"/${_key}", null).normalize

  // We use URI as a way to sanitize things (dropping extra /)
  lazy val bucket: String = toURI.getHost
  lazy val key: String = toURI.getPath

  lazy val segments: Seq[String] = key.split("/").filter(_.nonEmpty).toSeq

  @deprecated("Use toURI method instead, or just toString", since = "v0.17.0")
  final def url = "s3://" + bucket + "/" + key

  override def toString = toURI.toString

  def toHttpsURL(region: Region): URL = new URL("https", s"s3-${region}.amazonaws.com", s"${bucket}/${key}")
}


case class S3Folder(b: String, k: String) extends AnyS3Address {
  val _bucket = b
  // NOTE: we explicitly add / in the end here (it represents the empty S3 object of the folder)
  val _key = k + "/"

  def /(suffix: String): S3Object = S3Object(bucket, key + suffix)
}

object S3Folder {

  def apply(uri: URI): S3Folder = S3Folder(uri.getHost, uri.getPath)

  implicit def toS3Object(f: S3Folder): S3Object =
    S3Object(f.bucket, f.key.stripSuffix("/"))
}


case class S3Object(_bucket: String, _key: String) extends AnyS3Address {

  def /(): S3Folder = S3Folder(bucket, key)

  def /(suffix: String): S3Object = this./ / suffix
}

object S3Object {

  def apply(uri: URI): S3Object = S3Object(uri.getHost, uri.getPath)
}


case class S3AddressFromString(val sc: StringContext) extends AnyVal {

  // This allows to write things like s3"bucket" / "foo" / "bar" /
  // or s3"org.com/${suffix}/${folder.getName}" / "file.foo"
  def s3(args: Any*): S3Folder = {
    val str = sc.s(args: _*)
    S3Folder(new URI("s3://" + str))
  }
}

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md