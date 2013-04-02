package ohnosequences.awstools.autoscaling

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.AmazonAutoScalingClient
import com.amazonaws.services.autoscaling.model._

import scala.collection.JavaConversions._
import ohnosequences.awstools.ec2.Utils
import com.amazonaws.AmazonServiceException

class AutoScaling(val as: AmazonAutoScaling, ec2: ohnosequences.awstools.ec2.EC2) { autoscaling =>

  def shutdown() {
    as.shutdown()
  }

  def createLaunchingConfiguration(launchConfiguration: ohnosequences.awstools.autoscaling.LaunchConfiguration) {
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

  def createAutoScalingGroup(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup) {
    createLaunchingConfiguration(autoScalingGroup.launchingConfiguration)
    as.createAutoScalingGroup(new CreateAutoScalingGroupRequest()
      .withAutoScalingGroupName(autoScalingGroup.name)
      .withLaunchConfigurationName(autoScalingGroup.launchingConfiguration.name)
      .withAvailabilityZones(autoScalingGroup.availabilityZones)
      .withMaxSize(autoScalingGroup.maxSize)
      .withMinSize(autoScalingGroup.minSize)
      .withDesiredCapacity(autoScalingGroup.desiredCapacity)
    )
  }

//  def describeInstances(name: String): List[ohnosequences.awstools.ec2.Instance] = {
//    as.describeAutoScalingInstances(new DescribeAutoScalingInstancesRequest()).getAutoScalingInstances.map{ instanceDetails =>
//      ec2.getInstanceById(instanceDetails.getInstanceId)
//    }.toList
//  }
//
//  def getResourceId(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup): String = {
//    as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
//      .withAutoScalingGroupNames(autoScalingGroup.name)
//    ).getAutoScalingGroups.head.getAutoScalingGroupARN
//  }



  def createTags(name: String, tags: ohnosequences.awstools.ec2.Tag*) {
    val asTags = tags.map { tag =>
      new Tag().withKey(tag.name).withValue(tag.value).withResourceId(name).withPropagateAtLaunch(true).withResourceType("auto-scaling-group")
    }
    as.createOrUpdateTags(new CreateOrUpdateTagsRequest()
      .withTags(asTags)
    )
  }

  def getLaunchConfigurationByName(name: String): Option[ohnosequences.awstools.autoscaling.LaunchConfiguration] = {
    as.describeLaunchConfigurations(new DescribeLaunchConfigurationsRequest()
      .withLaunchConfigurationNames(name)
    ).getLaunchConfigurations.map {
      lc => ohnosequences.awstools.autoscaling.LaunchConfiguration.fromAWS(lc)
    }.headOption
  }

  def getAutoScalingGroupByName(name: String): Option[ohnosequences.awstools.autoscaling.AutoScalingGroup] = {
    as.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
      .withAutoScalingGroupNames(name)
    ).getAutoScalingGroups.flatMap {asg =>
      ohnosequences.awstools.autoscaling.AutoScalingGroup.fromAWS(asg, autoscaling)
    }.headOption
  }

  def describeLaunchConfigurations(): List[ohnosequences.awstools.autoscaling.LaunchConfiguration] = {
    as.describeLaunchConfigurations().getLaunchConfigurations.map {lc => ohnosequences.awstools.autoscaling.LaunchConfiguration.fromAWS(lc)}.toList
  }

  def describeAutoScalingGroups(): List[ohnosequences.awstools.autoscaling.AutoScalingGroup] = {
    as.describeAutoScalingGroups().getAutoScalingGroups.map {asg =>
      ohnosequences.awstools.autoscaling.AutoScalingGroup.fromAWS(asg, autoscaling)
    }.flatten.toList
  }

  def deleteLaunchConfiguration(name: String) {
    try {
    as.deleteLaunchConfiguration(
      new DeleteLaunchConfigurationRequest()
        .withLaunchConfigurationName(name)
    )
  } catch {
    case e: AmazonServiceException   => ;
  }
  }

  def deleteAutoScalingGroup(name: String) {
    getAutoScalingGroupByName(name).map(deleteAutoScalingGroup(_))
  }

  def setDesiredCapacity(group: ohnosequences.awstools.autoscaling.AutoScalingGroup, capacity: Int) {
    as.updateAutoScalingGroup(new UpdateAutoScalingGroupRequest()
      .withAutoScalingGroupName(group.name)
      .withDesiredCapacity(capacity)
    )
  }

//  def getDesiredCapacity = {
//
//  }

//  * <b>NOTE:</b> To remove all instances before calling
//    * DeleteAutoScalingGroup, you can call UpdateAutoScalingGroup to set the
//  * minimum and maximum size of the AutoScalingGroup to zero.
//  * </p>
  def deleteAutoScalingGroup(autoScalingGroup: ohnosequences.awstools.autoscaling.AutoScalingGroup) {
    try {
      as.deleteAutoScalingGroup(
        new DeleteAutoScalingGroupRequest()
          .withAutoScalingGroupName(autoScalingGroup.name)
          .withForceDelete(true)
      )
    } catch {
      case e: AmazonServiceException   => ;
    }
    finally {
      deleteLaunchConfiguration(autoScalingGroup.launchingConfiguration.name)
    }

  }

}

object AutoScaling {
  def create(credentialsFile: File, ec2: ohnosequences.awstools.ec2.EC2): AutoScaling = {
    val asClient = new AmazonAutoScalingClient(new PropertiesCredentials(credentialsFile))
    asClient.setEndpoint("http://autoscaling.eu-west-1.amazonaws.com")
    new AutoScaling(asClient, ec2)
  }
}
