
```scala
package ohnosequences.awstools.s3

import ohnosequences.awstools.regions._
import java.net.{ URI, URL }


sealed trait AnyS3Address {
  // These are the inputs
  val _bucket: String
  val _key: String

  def toURI: URI = new URI("s3", _bucket, s"/${_key}", null).normalize

  // We use URI as a way to sanitize things (dropping extra /)
  lazy val bucket: String = toURI.getHost
  lazy val key: String = toURI.getPath.stripPrefix("/")

  lazy val segments: Seq[String] = key.split("/").filter(_.nonEmpty).toSeq

  @deprecated("Use toURI method instead, or just toString", since = "v0.17.0")
  final def url: String = "s3://" + bucket + "/" + key

  override def toString: String = toURI.toString

  def toHttpsURL(region: Regions): URL = new URL("https", s"s3-${region}.amazonaws.com", s"${bucket}/${key}")
}


case class S3Folder(b: String, k: String) extends AnyS3Address {
  val _bucket = b
  // NOTE: we explicitly add / in the end here (it represents the empty S3 object of the folder)
  val _key = k + "/"

  def /(suffix: String): S3Object = S3Object(bucket, key + suffix)
}

object S3Folder {

  def apply(uri: URI): S3Folder = S3Folder(uri.getHost, uri.getPath)

  def toS3Object(f: S3Folder): S3Object =
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
  def s3(args: Any*): S3Object = {
    val str = sc.s(args: _*)
    S3Object(new URI("s3://" + str))
  }
}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md