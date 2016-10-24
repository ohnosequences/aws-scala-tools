package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model
import com.amazonaws.services.ec2.model.{ Instance => JavaInstance, _ }
import scala.collection.JavaConversions._
import scala.util.Try
import java.util.Date


case class Instance(
  val ec2: AmazonEC2,
  val asJava: JavaInstance
) { instance =>

  /* ### Instance parameters

    These are either direct copies of the SDK methods, just as lazy vals where possible (things that are not supposed to change during the instance lifetime), or simply improved versions that return corresponding enumeration values instead of Strings.
  */

  lazy val id:        String = asJava.getInstanceId
  lazy val publicDNS: String = asJava.getPublicDnsName
  lazy val publicIP:  String = asJava.getPublicIpAddress
  lazy val keyName:   String = asJava.getKeyName
  lazy val amiID:     String = asJava.getImageId
  lazy val launchTime:  Date = asJava.getLaunchTime

  /* Either `I386` or `X86_64` */
  lazy val architecture: ArchitectureValues = ArchitectureValues.fromValue(asJava.getArchitecture)

  /* Either `Ebs` or `InstanceStore` */
  lazy val rootDevice: DeviceType = DeviceType.fromValue(asJava.getRootDeviceType)

  /* Either `Hvm` or `Paravirtual` */
  lazy val virtualization: VirtualizationType = VirtualizationType.fromValue(asJava.getVirtualizationType)

  /* Either `Ovm` or `Xen` */
  lazy val hypervisor: HypervisorType = HypervisorType.fromValue(asJava.getHypervisor)

  /* Either `Scheduled` or `Spot` */
  lazy val lifecycle: InstanceLifecycleType = InstanceLifecycleType.fromValue(asJava.getInstanceLifecycle)

  lazy val instanceType: model.InstanceType = model.InstanceType.fromValue(asJava.getInstanceType)

  /* This will be `None` if the instance wasn't launched from a spot request */
  lazy val spotRequestId: Option[String] = Option(asJava.getSpotInstanceRequestId).filter(_.nonEmpty)


  /* ### Actions on the instance

    These methods involve requests on behalf of the EC2 client.
  */

  def terminate: Try[Unit] = Try { ec2.terminateInstance(instance.id) }

  /* This method makes a `DescribeInstanceStatusRequest` and is supposed to be used with the implicit "shortcuts" from the `ec2._` package object returning corresponding enumeration values. Having `st: InstanceStatus`, you can use

    - `st.stateName: InstanceStateName`
    - `st.instanceSummary: SummaryStatus`
    - `st.systemSummary: SummaryStatus`
  */
  def status: Try[InstanceStatus] = Try {
    ec2.describeInstanceStatus(
      new DescribeInstanceStatusRequest().withInstanceIds(instance.id)
    ).getInstanceStatuses
      .headOption.getOrElse {
        throw new java.util.NoSuchElementException(s"Instance [${instance.id}] doesn't exist")
      }
  }

  /* Can be one of `Disabled`, `Disabling`, `Enabled`, `Pending` */
  // TODO: def monitoring: Try[MonitoringState] = ???

  // def createTags(tags: List[InstanceTag]): Unit = {
  //   ec2.createTags(instance.id, tags)
  // }
  //
  // def getTagValue(tagName: String): Option[String] = {
  //   getEC2Instance().getTags.find(_.getKey == tagName).map(_.getValue)
  // }
}
