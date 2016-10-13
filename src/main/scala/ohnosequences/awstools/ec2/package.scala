package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.ec2.{ AmazonEC2, AmazonEC2Client }
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._

package object ec2 {

  def EC2Client(
    region: Region,
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonEC2Client = {
    new AmazonEC2Client(credentials, configuration)
      .withRegion(region.toAWSRegion)
  }


  lazy val metadataLocalURL      = new java.net.URL("http://169.254.169.254/latest/meta-data")
  // lazy val metadataLocalAMIIdURL = new URL(metadataLocalURL, "ami-id")

  def base64encode(input: String) = new sun.misc.BASE64Encoder().encode(input.getBytes())

  def stringToOption(s: String): Option[String] = {
    if(s == null || s.isEmpty) None else Some(s)
  }


  // Implicits
  implicit def toScalaEC2Client(ec2: AmazonEC2):
    ScalaEC2Client =
    ScalaEC2Client(ec2)
}
