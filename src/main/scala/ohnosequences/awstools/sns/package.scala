package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sns.{ AmazonSNS, AmazonSNSClient }
import ohnosequences.awstools.regions._

package object sns {

  def client(
    region: Region,
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain()
  ): AmazonSNSClient = {
    new AmazonSNSClient(credentials)
      .withRegion(region.toAWSRegion)
  }

  // Implicits
  implicit def toScalaSNSClient(sns: AmazonSNS):
    ScalaSNSClient =
    ScalaSNSClient(sns)
}
