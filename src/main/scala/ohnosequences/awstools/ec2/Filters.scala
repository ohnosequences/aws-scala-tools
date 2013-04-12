package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2

import scala.collection.JavaConversions._

case class Tag(name: String, value: String) {
  def toECTag = new ec2.model.Tag(name, value)
}

sealed abstract class Filter {
  def toEC2Filter: ec2.model.Filter
}

case class ResourceFilter(id: String) extends  Filter {
  override def toEC2Filter =  new ec2.model.Filter("resource-id:", List(id))
}

case class TagFilter(tag: Tag) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("tag:" + tag.name, List(tag.value))
}

//case class AutoScalingGroupFilter(groupName: String) extends Filter {
//  override def toEC2Filter = new ec2.model.Filter("aws:autoscaling:groupName", List(groupName))
//}

case class RequestStateFilter(states: String*) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("state", states)
}

case class InstanceStateFilter(states: String*) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("instance-state-name", states)
}


