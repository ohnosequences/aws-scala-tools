package ohnosequences.saws.signing

import com.amazonaws.auth.PropertiesCredentials
import java.io.File

case class Credentials(accessKey: String, secretKey: String)

object Credentials {
  def fromFile(fileName: String): Credentials = {
    val cred = new PropertiesCredentials(new File(fileName))
    Credentials(cred.getAWSAccessKeyId, cred.getAWSSecretKey)
  }
}
