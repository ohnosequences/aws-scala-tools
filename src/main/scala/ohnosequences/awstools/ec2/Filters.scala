package ohnosequences.awstools.ec2

import scala.collection.JavaConversions._

import com.amazonaws.{ services => amzn }


case class InstanceTag(name: String, value: String) {
  def toECTag = new amzn.ec2.model.Tag(name, value)
}

sealed abstract class InstanceFilter(val toEC2Filter: amzn.ec2.model.Filter)

case class ResourceFilter(id: String) extends InstanceFilter(
  new amzn.ec2.model.Filter("resource-id:", List(id))
)

case class TagFilter(tag: InstanceTag) extends InstanceFilter(
  new amzn.ec2.model.Filter("tag:" + tag.name, List(tag.value))
)

//case class AutoScalingGroupFilter(groupName: String) extends InstanceFilter(
//   new amzn.ec2.model.Filter("aws:autoscaling:groupName", List(groupName))
// )

case class RequestStateFilter(states: String*) extends InstanceFilter(
  new amzn.ec2.model.Filter("state", states)
)

case class InstanceStateFilter(states: String*) extends InstanceFilter(
  new amzn.ec2.model.Filter("instance-state-name", states)
)
