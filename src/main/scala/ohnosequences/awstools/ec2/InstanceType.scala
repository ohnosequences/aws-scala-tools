package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

sealed class InstanceType private(val name: String) {
  override def toString = name

  def toAWS = JavaInstanceType.fromValue(name)
}

object InstanceType {

  implicit def toJavaInstanceType(t: InstanceType): JavaInstanceType =
    JavaInstanceType.fromValue(t.name)

  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  def fromName(name: String): InstanceType = new InstanceType(name)

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html

  // TODO: write tests that check that the string name corresponds to the object name
  // Current Generation Instances //

  // General purpose
  case object t2 {
    case object micro  extends InstanceType("t2.micro")
    case object small  extends InstanceType("t2.small")
    case object medium extends InstanceType("t2.medium")
    case object large  extends InstanceType("t2.large")
  }
  case object m4 {
    case object large    extends InstanceType("m4.large")
    case object xlarge   extends InstanceType("m4.xlarge")
    case object x2large  extends InstanceType("m4.2xlarge")
    case object x4large  extends InstanceType("m4.4xlarge")
    case object x10large extends InstanceType("m4.10xlarge")
  }
  case object m3 {
    case object medium  extends InstanceType("m3.medium")
    case object large   extends InstanceType("m3.large")
    case object xlarge  extends InstanceType("m3.xlarge")
    case object x2large extends InstanceType("m3.2xlarge")
  }

  // Compute optimized
  case object c4 {
    case object large   extends InstanceType("c4.large")
    case object xlarge  extends InstanceType("c4.xlarge")
    case object x2large extends InstanceType("c4.2xlarge")
    case object x4large extends InstanceType("c4.4xlarge")
    case object x8large extends InstanceType("c4.8xlarge")
  }
  case object c3 {
    case object large   extends InstanceType("c3.large")
    case object xlarge  extends InstanceType("c3.xlarge")
    case object x2large extends InstanceType("c3.2xlarge")
    case object x4large extends InstanceType("c3.4xlarge")
    case object x8large extends InstanceType("c3.8xlarge")
  }

  // Memory optimized
  case object r3 {
    case object large   extends InstanceType("r3.large")
    case object xlarge  extends InstanceType("r3.xlarge")
    case object x2large extends InstanceType("r3.2xlarge")
    case object x4large extends InstanceType("r3.4xlarge")
    case object x8large extends InstanceType("r3.8xlarge")
  }

  // Storage optimized
  case object i2 {
    case object xlarge  extends InstanceType("i2.xlarge")
    case object x2large extends InstanceType("i2.2xlarge")
    case object x4large extends InstanceType("i2.4xlarge")
    case object x8large extends InstanceType("i2.8xlarge")
  }

  case object d2 {
    case object xlarge  extends InstanceType("d2.xlarge")
    case object x2large extends InstanceType("d2.2xlarge")
    case object x4large extends InstanceType("d2.4xlarge")
    case object x8large extends InstanceType("d2.8xlarge")
  }


  // Previous Generation Instances //

  // General purpose
  case object m1 {
    case object small  extends InstanceType("m1.small")
    case object medium extends InstanceType("m1.medium")
    case object large  extends InstanceType("m1.large")
    case object xlarge extends InstanceType("m1.xlarge")
  }

  // Compute optimized
  case object c1 {
    case object medium  extends InstanceType("c1.medium")
    case object xlarge  extends InstanceType("c1.xlarge")
  }
  case object cc2 {
    case object x8large extends InstanceType("cc2.8xlarge")
  }

  // Memory optimized
  case object m2 {
    case object xlarge  extends InstanceType("m2.xlarge")
    case object x2large extends InstanceType("m2.2xlarge")
    case object x4large extends InstanceType("m2.4xlarge")
  }
  case object cr1 {
    case object x8large extends InstanceType("cr1.8xlarge")
  }

  // Storage optimized
  case object hi1 {
    case object x4large extends InstanceType("hi1.4xlarge")
  }
  case object hs1 {
    case object x8large extends InstanceType("hs1.8xlarge")
  }

  // GPU instances
  case object cg1 {
    case object x4large extends InstanceType("cg1.4xlarge")
  }

  // Micro instances
  case object t1 {
    case object micro extends InstanceType("t1.micro")
  }

}
