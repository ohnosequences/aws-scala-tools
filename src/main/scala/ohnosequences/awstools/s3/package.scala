package ohnosequences.awstools

package object s3 {

  // Just an alias for the "root" S3 fodler:
  def S3Bucket(b: String): S3Folder = S3Folder(b, "")

  implicit def s3AddressFromString(sc: StringContext):
    S3AddressFromString =
    S3AddressFromString(sc)

}
