
```scala
package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.{ AmazonAutoScaling, AmazonAutoScalingClient }
import com.amazonaws.services.autoscaling.model._
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import com.amazonaws.waiters._
import ohnosequences.awstools.regions._


package object autoscaling {

  def AutoScalingClient(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonAutoScalingClient = {
    new AmazonAutoScalingClient(credentials, configuration)
      .withRegion(region)
  }

  // Implicits
  implicit def toScalaAutoScalingClient(autoscaling: AmazonAutoScaling):
    ScalaAutoScalingClient =
    ScalaAutoScalingClient(autoscaling)
```

`TagDescription` is essentially the same as `Tag`, but for some strange reason in Amazon SDK they are not related anyhow

```scala
  implicit def tagDescriptionToTag(td: TagDescription): Tag =
    new Tag()
      .withKey(td.getKey)
      .withPropagateAtLaunch(td.isPropagateAtLaunch)
      .withResourceId(td.getResourceId)
      .withResourceType(td.getResourceType)
      .withValue(td.getValue)
```

Waiting for the group to transition to a certain state. This is useful, for example, immediately after creating a group

```scala
  type GroupWaiter = Waiter[DescribeAutoScalingGroupsRequest]

  implicit class waitersOps(val waiter: GroupWaiter) extends AnyVal {

    def withName(groupName: String): Unit = {

      waiter.run(
        new WaiterParameters(
          new DescribeAutoScalingGroupsRequest()
            .withAutoScalingGroupNames(groupName)
        )
      )
    }
  }
}

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