package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._
import java.util.Date

case class AutoScalingGroup(
  val launchConfiguration: LaunchConfiguration,
  val name: String = "",
  val minSize: Int,
  val desiredCapacity: Int,
  val maxSize: Int,
  val availabilityZones: List[String] = List("eu-west-1a", "eu-west-1b", "eu-west-1c")
)

case object AutoScalingGroup {

  def fromAWS(autoScalingGroup: amzn.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
    autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
      case None => None //since launch configuration deleted this autoscaling group will be deleted soon
      case Some(launchConfiguration) => Some(AutoScalingGroup(
        launchConfiguration,
        name = autoScalingGroup.getAutoScalingGroupName,
        minSize = autoScalingGroup.getMinSize,
        maxSize = autoScalingGroup.getMaxSize,
        desiredCapacity = autoScalingGroup.getDesiredCapacity
      ))
    }
  }
}
