
```scala
package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.ec2.{ AmazonEC2, AmazonEC2Client }
import com.amazonaws.services.ec2.model.{ Instance => JavaInstance, _ }
import com.amazonaws.services.ec2.waiters._
import com.amazonaws.waiters._
import com.amazonaws.ClientConfiguration
import com.amazonaws.PredefinedClientConfigurations
import ohnosequences.awstools.regions._
import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.Try
import java.net.URL

package object ec2 {

  def EC2Client(
    region: AwsRegionProvider = new DefaultAwsRegionProviderChain(),
    credentials: AWSCredentialsProvider = new DefaultAWSCredentialsProviderChain(),
    configuration: ClientConfiguration = PredefinedClientConfigurations.defaultConfig()
  ): AmazonEC2Client = {
    new AmazonEC2Client(credentials, configuration)
      .withRegion(region)
  }


  val localMetadataURL = new URL("http://169.254.169.254/latest/meta-data/")

  def getLocalMetadata(path: String): Try[String] = Try {
    Source.fromURL(
      new URL(localMetadataURL, path)
    ).mkString
  }


  def base64encode(input: String) = new sun.misc.BASE64Encoder().encode(input.getBytes())

  def stringToOption(s: String): Option[String] = {
    if(s == null || s.isEmpty) None else Some(s)
  }


  // Implicits
  implicit def toScalaEC2Client(ec2: AmazonEC2):
    ScalaEC2Client =
    ScalaEC2Client(ec2)

  implicit def toJavaInstance(instance: Instance): JavaInstance = instance.asJava

  implicit class InstanceStateOps(val state: InstanceState) extends AnyVal {

    def name: InstanceStateName = InstanceStateName.fromValue(state.getName)
  }

  implicit class InstanceStatusSummaryOps(val statusSummary: InstanceStatusSummary) extends AnyVal {

    def summary: SummaryStatus = SummaryStatus.fromValue(statusSummary.getStatus)
  }

  implicit class InstanceStatusOps(val status: InstanceStatus) extends AnyVal {

    def stateName: InstanceStateName = status.getInstanceState.name

    def instanceSummary: SummaryStatus = status.getInstanceStatus.summary
    def   systemSummary: SummaryStatus = status.getSystemStatus.summary
  }
```

Waiting for the instances or spot-requests to transition to a certain state

```scala
  // type InstancesWaiter = Waiter[DescribeAutoScalingInstancesRequest]
  // type SpotRequestsWaiter = Waiter[DescribeSpotInstanceRequestsRequest]

  implicit class instanceWaiterOps(val waiter: Waiter[DescribeInstancesRequest]) extends AnyVal {

    def withIDs(instanceIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeInstancesRequest().withInstanceIds(instanceIDs)
      )
    )
  }

  implicit class instanceStatusWaiterOps(val waiter: Waiter[DescribeInstanceStatusRequest]) extends AnyVal {

    def withIDs(instanceIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeInstanceStatusRequest().withInstanceIds(instanceIDs)
      )
    )
  }

  implicit class spotRequestWaiterOps(val waiter: Waiter[DescribeSpotInstanceRequestsRequest]) extends AnyVal {

    def withIDs(requestIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(requestIDs)
      )
    )
  }
}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: package.scala.md
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