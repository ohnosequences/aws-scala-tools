
```scala
package ohnosequences.awstools.autoscaling

import com.amazonaws.services.autoscaling.model._
import scala.collection.JavaConversions._
```

This is a enum-type for the tags `Filter` from the SDK. Motivation for this wrapper is that the filter name can have only 4 fixed `String` values.

```scala
sealed abstract class AutoScalingTagFilter(name: String, values: Seq[String]) {

  val asJava = new Filter()
    .withName(name)
    .withValues(values)
}

case class ByGroupNames(groups: String*)       extends AutoScalingTagFilter("auto-scaling-group", groups)
case class ByTagKeys(keys: String*)            extends AutoScalingTagFilter("key", keys)
case class ByTagValues(values: String*)        extends AutoScalingTagFilter("value", values)
case class ByPropagateAtLaunch(value: Boolean) extends AutoScalingTagFilter("propagate-at-launch", Seq(value.toString))

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md