package ohnosequences.awstools.autoscaling

import ohnosequences.awstools.ec2._
import com.amazonaws.{ services => amzn }
import scala.collection.JavaConversions._
import java.util.Date

case class AutoScalingGroupSize(
  min: Int,
  desired: Int,
  max: Int
)

case class AutoScalingGroup(
  val launchConfiguration: LaunchConfiguration,
  val name: String,
  val size: AutoScalingGroupSize,
  val availabilityZones: List[String] = List()
)

case object AutoScalingGroup {

  def fromAWS(autoScalingGroup: amzn.autoscaling.model.AutoScalingGroup, autoscaling: AutoScaling): Option[AutoScalingGroup] = {
    autoscaling.getLaunchConfigurationByName(autoScalingGroup.getLaunchConfigurationName) match {
      case None => None //since launch configuration deleted this autoscaling group will be deleted soon
      case Some(launchConfiguration) => Some(AutoScalingGroup(
        launchConfiguration,
        name = autoScalingGroup.getAutoScalingGroupName,
        size = AutoScalingGroupSize(
          autoScalingGroup.getMinSize,
          autoScalingGroup.getDesiredCapacity,
          autoScalingGroup.getMaxSize
        )
      ))
    }
  }
}
