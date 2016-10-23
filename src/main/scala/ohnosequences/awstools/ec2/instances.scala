package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.AmazonEC2
import com.amazonaws.services.ec2.model.{ Instance => JavaInstance, _ }
import scala.collection.JavaConversions._
import scala.util.Try
import java.util.Date


case class InstanceStatus(val instanceStatus: String, val systemStatus: String)


case class Instance(
  val ec2: AmazonEC2,
  val asJava: JavaInstance
) { instance =>

  /* ### Instance parameters

    These are either direct copies of the SDK methods, just as lazy vals where possible (things that are not supposed to change during the instance lifetime), or simply improved versions that return corresponding enumeration values instead of just Strings.
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

  // NOTE: this is the scala type, which should be convertible to the corresponding SDK enum
  lazy val instanceType: AnyInstanceType = InstanceType.fromName(asJava.getInstanceType)

  /* This will be `None` if the instance wasn't launched from a spot request */
  lazy val spotRequestId: Option[String] = Option(asJava.getSpotInstanceRequestId).filter(_.nonEmpty)

  /* Can be one of `Disabled`, `Disabling`, `Enabled`, `Pending` */
  // NOTE: monitoring can be turned on/off with the client's (un)monitorInstances requests
  def monitoringState: MonitoringState = MonitoringState.fromValue(asJava.getMonitoring.getState)


  def state: InstanceStateName = InstanceStateName.fromValue(asJava.getState.getName)

  // TODO: getStateReason with a corresponding enum of reasons


  /* ### Actions on the instance

    These methods involve requests on behalf of the EC2 client.
  */

  def terminate: Try[Unit] = Try { ec2.terminateInstance(instance.id) }

  // def createTags(tags: List[InstanceTag]): Unit = {
  //   ec2.createTags(instance.id, tags)
  // }
  //
  // def getTagValue(tagName: String): Option[String] = {
  //   getEC2Instance().getTags.find(_.getKey == tagName).map(_.getValue)
  // }

  // def getStatus(): Option[InstanceStatus] = {
  //   val statuses = ec2.describeInstanceStatus(new model.DescribeInstanceStatusRequest()
  //     .withInstanceIds(instance.id)
  //     ).getInstanceStatuses()
  //   if (statuses.isEmpty) None
  //   else {
  //     val is = statuses.head
  //     Some(InstanceStatus(
  //         is.getInstanceStatus().getStatus()
  //       , is.getSystemStatus().getStatus()
  //       )
  //     )
  //   }
  // }
}
