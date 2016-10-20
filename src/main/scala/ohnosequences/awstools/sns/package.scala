package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.sns.{ AmazonSNS, AmazonSNSClient }
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._

package object sns {

  def SNSClient(
    region: Region,
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonSNSClient = {
    new AmazonSNSClient(credentials, configuration)
      .withRegion(region.toAWSRegion)
  }

  // Implicits
  implicit def toScalaSNSClient(sns: AmazonSNS):
    ScalaSNSClient =
    ScalaSNSClient(sns)
}
