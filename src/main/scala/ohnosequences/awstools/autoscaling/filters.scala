package ohnosequences.awstools.autoscaling

import com.amazonaws.services.autoscaling.model._
import scala.collection.JavaConversions._


/* This is a enum-type for the tags `Filter` from the SDK. Motivation for this wrapper is that the filter name can have only 4 fixed `String` values. */
sealed abstract class AutoScalingTagFilter(name: String, values: Seq[String]) {

  val asJava = new Filter()
    .withName(name)
    .withValues(values)
}

case class ByGroupNames(groups: String*)       extends AutoScalingTagFilter("auto-scaling-group", groups)
case class ByTagKeys(keys: String*)            extends AutoScalingTagFilter("key", keys)
case class ByTagValues(values: String*)        extends AutoScalingTagFilter("value", values)
case class ByPropagateAtLaunch(value: Boolean) extends AutoScalingTagFilter("propagate-at-launch", Seq(value.toString))
