package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.{ AmazonEC2, model }
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

  lazy val tagsMap: Map[String, String] = instance.asJava.getTags.map { tag =>
    tag.getKey -> tag.getValue
  }.toMap


  /* ### Actions on the instance

    These methods involve requests on behalf of the EC2 client.
  */

  def terminate: Try[Unit] = Try { ec2.terminateInstances(new TerminateInstancesRequest(List(instance.id))) }
  def reboot:    Try[Unit] = Try {    ec2.rebootInstances(new RebootInstancesRequest(List(instance.id))) }

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


  /* It doesn't seems to be possible to get this from the SDK's `Instance` type itself (unlike the rest of attributes). So it's the only attribute exposed in this API. */
  def userData: Try[String] = Try {
    ec2.describeInstanceAttribute(
      new DescribeInstanceAttributeRequest(instance.id, InstanceAttributeName.UserData)
    ).getInstanceAttribute.getUserData
  }

  /* Enables or disables instance monitoring and returnes the monitoring state. It can be one of `Disabled`, `Disabling`, `Enabled` or `Pending`. */
  def setMonitoring(on: Boolean): Try[MonitoringState] = Try {

    if (on) ec2.monitorInstances(new   MonitorInstancesRequest(List(instance.id))).getInstanceMonitorings
    else  ec2.unmonitorInstances(new UnmonitorInstancesRequest(List(instance.id))).getInstanceMonitorings
  }.map { monitorings =>

    val monitoring: Monitoring = monitorings.headOption.getOrElse(
      throw new java.util.NoSuchElementException(s"Instance [${instance.id}] doesn't exist")
    ).getMonitoring

    MonitoringState.fromValue(monitoring.getState)
  }


  def createTags(tags: Map[String, String]): Try[Unit] = Try {
    ec2.createTags(new CreateTagsRequest()
      .withResources(instance.id)
      .withTags(tags.map { case (key, value) => new Tag(key, value) }.toList)
    )
  }

  def deleteTags(keys: Set[String]): Try[Unit] = Try {
    ec2.deleteTags(new DeleteTagsRequest()
      .withResources(instance.id)
      .withTags(keys.map { key => new Tag(key) })
    )
  }

}
