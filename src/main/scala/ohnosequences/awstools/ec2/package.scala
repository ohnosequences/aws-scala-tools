package ohnosequences.awstools

package object ec2 {

  lazy val metadataLocalURL      = new java.net.URL("http://169.254.169.254/latest/meta-data")
  // lazy val metadataLocalAMIIdURL = new URL(metadataLocalURL, "ami-id")

  def base64encode(input: String) = new sun.misc.BASE64Encoder().encode(input.getBytes())

  def stringToOption(s: String): Option[String] = {
    if(s == null || s.isEmpty) None else Some(s)
  }

}
