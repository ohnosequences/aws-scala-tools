package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.{ AmazonAutoScaling, AmazonAutoScalingClient }
import com.amazonaws.services.autoscaling.model._
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._


package object autoscaling {

  def AutoScalingClient(
    region: Region,
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonAutoScalingClient = {
    new AmazonAutoScalingClient(credentials, configuration)
      .withRegion(region.toAWSRegion)
  }

  // Implicits
  implicit def toScalaAutoScalingClient(autoscaling: AmazonAutoScaling):
    ScalaAutoScalingClient =
    ScalaAutoScalingClient(autoscaling)

  /* `TagDescription` is essentially the same as `Tag`, but for some strange reason in Amazon SDK they are not related anyhow */
  implicit def tagDescriptionToTag(td: TagDescription): Tag =
    new Tag()
      .withKey(td.getKey)
      .withPropagateAtLaunch(td.isPropagateAtLaunch)
      .withResourceId(td.getResourceId)
      .withResourceType(td.getResourceType)
      .withValue(td.getValue)
}
