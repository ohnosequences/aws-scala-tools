package ohnosequences.awstools.s3

import com.amazonaws.services.s3.AmazonS3
import java.io.File
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}

case class Bucket(val s3: AmazonS3, val name: String) {

  def putObject(file: File, public: Boolean = false) {
    if (public) {
      s3.putObject(new PutObjectRequest(name, file.getName, file).withCannedAcl(CannedAccessControlList.PublicRead))
    } else {
      s3.putObject(new PutObjectRequest(name, file.getName, file))
    }
  }

  def delete {
    s3.deleteBucket(name)
  }



//  def getUrl = {
//    s3.getBucketLocation(name)
//  }
}
