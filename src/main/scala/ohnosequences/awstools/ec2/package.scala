package ohnosequences.awstools

import com.amazonaws.auth._
import com.amazonaws.services.ec2.{ AmazonEC2, AmazonEC2Client }
import com.amazonaws.services.ec2.model.{ Instance => JavaInstance, _ }
import com.amazonaws.waiters._
import com.amazonaws.{ ClientConfiguration, PredefinedClientConfigurations }
import ohnosequences.awstools.regions._
import scala.collection.JavaConverters._
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

  /* Waiting for the instances or spot-requests to transition to a certain state */
  // type InstancesWaiter = Waiter[DescribeAutoScalingInstancesRequest]
  // type SpotRequestsWaiter = Waiter[DescribeSpotInstanceRequestsRequest]

  implicit class instanceWaiterOps(val waiter: Waiter[DescribeInstancesRequest]) extends AnyVal {

    def withIDs(instanceIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeInstancesRequest().withInstanceIds(instanceIDs.asJava)
      )
    )
  }

  implicit class instanceStatusWaiterOps(val waiter: Waiter[DescribeInstanceStatusRequest]) extends AnyVal {

    def withIDs(instanceIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeInstanceStatusRequest().withInstanceIds(instanceIDs.asJava)
      )
    )
  }

  implicit class spotRequestWaiterOps(val waiter: Waiter[DescribeSpotInstanceRequestsRequest]) extends AnyVal {

    def withIDs(requestIDs: Seq[String]): Unit = waiter.run(
      new WaiterParameters(
        new DescribeSpotInstanceRequestsRequest().withSpotInstanceRequestIds(requestIDs.asJava)
      )
    )
  }
}
