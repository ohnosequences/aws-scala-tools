package ohnosequences.awstools.utils

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.model.{Instance, DescribeAutoScalingGroupsRequest}

import scala.collection.JavaConversions._

import scala.util.Try


/**
 * Created by Evdokim on 18.06.2015.
 */
object AutoScalingUtils {
  def describeInstances(as: AmazonAutoScaling,
                        groupName: String,
                        lastToken: Option[String],
                        limit: Option[Int]): Try[(Option[String], List[Instance])] = {
    Try {
      val request = new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(groupName)
      limit.foreach { l => request.setMaxRecords(l) }
      lastToken.foreach { t => request.setNextToken(t) }
      val r = as.describeAutoScalingGroups(request)
      (Option(r.getNextToken), r.getAutoScalingGroups.flatMap { _.getInstances}.toList)
    }

  }

}
