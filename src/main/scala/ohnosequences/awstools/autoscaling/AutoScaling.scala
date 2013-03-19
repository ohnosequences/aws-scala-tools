package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model.{CreateLaunchConfigurationRequest, AlreadyExistsException, CreateAutoScalingGroupRequest, DeleteLaunchConfigurationRequest, DeleteAutoScalingGroupRequest}

import scala.collection.JavaConversions._
import ohnosequences.awstools.ec2.Utils


class AutoScaling(val as: AmazonAutoScaling) {

  def shutdown() {
    as.shutdown()
  }

  def createLaunchingConfiguration(launchConfiguration: LaunchConfiguration) {
    try {
      as.createLaunchConfiguration(new CreateLaunchConfigurationRequest()
        .withLaunchConfigurationName(launchConfiguration.name)
        .withSpotPrice(launchConfiguration.spotPrice.toString)
        .withImageId(launchConfiguration.instanceSpecs.amiId)
        .withInstanceType(launchConfiguration.instanceSpecs.instanceType.toString)
        .withUserData(Utils.base64encode(launchConfiguration.instanceSpecs.userData))
        .withKeyName(launchConfiguration.instanceSpecs.keyName)
        .withSecurityGroups(launchConfiguration.instanceSpecs.securityGroups)
      )
    } catch {
      case e: AlreadyExistsException => ;
    }
  }

  def createAutoScalingGroup(autoScalingGroup: AutoScalingGroup) {
    as.createAutoScalingGroup(new CreateAutoScalingGroupRequest()
      .withAutoScalingGroupName(autoScalingGroup.name)
      .withLaunchConfigurationName(autoScalingGroup.launchingConfiguration)
      .withAvailabilityZones(autoScalingGroup.availabilityZones)
      .withMaxSize(autoScalingGroup.maxSize)
      .withMinSize(autoScalingGroup.minSize)
      .withDesiredCapacity(autoScalingGroup.desiredCapacity)
    )
  }

  def describeLaunchConfigurations(): List[LaunchConfiguration] = {
    as.describeLaunchConfigurations().getLaunchConfigurations.map {lc => LaunchConfiguration.fromAWS(lc)}.toList
  }

  def describeAutoScalingGroups(): List[AutoScalingGroup] = {
    as.describeAutoScalingGroups().getAutoScalingGroups.map {asg => AutoScalingGroup.fromAWS(asg)}.toList
  }

  def deleteLaunchConfiguration(name: String) {
    as.deleteLaunchConfiguration(
      new DeleteLaunchConfigurationRequest()
        .withLaunchConfigurationName(name)
    )
  }

  def deleteAutoScalingGroup(name: String) {
    as.deleteAutoScalingGroup(
      new DeleteAutoScalingGroupRequest()
        .withAutoScalingGroupName(name)
        .withForceDelete(true)
    )
  }

}

object AutoScaling {
  def create(credentialsFile: File): AutoScaling = {
    val asClient = new AmazonAutoScalingClient(new PropertiesCredentials(credentialsFile))
    asClient.setEndpoint("http://autoscaling.eu-west-1.amazonaws.com")
    new AutoScaling(asClient)
  }
}
