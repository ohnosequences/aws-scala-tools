package ohnosequences.awstools.s3

import ohnosequences.awstools.regions._
import java.net.{ URI, URL }


sealed trait AnyS3Address {
  // These are the inputs
  val _bucket: String
  val _key: String

  // NOTE: java.net.URI needs an explicit null for the empty fragment argument
  @SuppressWarnings(Array("org.wartremover.warts.Null"))
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
