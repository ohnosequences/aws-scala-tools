
```scala
package ohnosequences.awstools.test

import ohnosequences.awstools._, autoscaling._
import ohnosequences.awstools.regions._
import com.amazonaws.auth._
import com.amazonaws.services.autoscaling.AmazonAutoScaling

class AutoScaling extends org.scalatest.FunSuite with org.scalatest.BeforeAndAfterAll {

  lazy val autoscaling: AmazonAutoScaling = AutoScalingClient()


  override def beforeAll() = {
  }

  override def afterAll() = {
  }

}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../../../../main/scala/ohnosequences/awstools/package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../../../../main/scala/ohnosequences/awstools/regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../../../../main/scala/ohnosequences/awstools/regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../../../../main/scala/ohnosequences/awstools/s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../../../../main/scala/ohnosequences/awstools/s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../../../../main/scala/ohnosequences/awstools/s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../../../../main/scala/ohnosequences/awstools/s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: ../../../../main/scala/ohnosequences/awstools/sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../../../../main/scala/ohnosequences/awstools/sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../../../../main/scala/ohnosequences/awstools/sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../../../../main/scala/ohnosequences/awstools/sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: sqs.scala.md