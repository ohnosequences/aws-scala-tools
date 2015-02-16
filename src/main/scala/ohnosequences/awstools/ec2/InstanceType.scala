package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

sealed class InstanceType private(val name: String) {
  override def toString = name

  //@deprecated("There is an implicit conversion for that in ohnosequences.awstools.ec2.InstanceType, just import it",
  //            since = "v0.6.0")
  def toAWS = JavaInstanceType.fromValue(name)
}

object InstanceType {

  implicit def toJavaInstanceType(t: InstanceType): JavaInstanceType = 
    JavaInstanceType.fromValue(t.name)

  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  def fromName(name: String): InstanceType = new InstanceType(name)

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html

  // Current Generation Instances //

  // General purpose
  case object t2_micro   extends InstanceType("t2.micro")
  case object t2_small   extends InstanceType("t2.small")
  case object t2_medium  extends InstanceType("t2.medium")
  case object m3_medium  extends InstanceType("m3.medium")
  case object m3_large   extends InstanceType("m3.large")
  case object m3_xlarge  extends InstanceType("m3.xlarge")
  case object m3_2xlarge extends InstanceType("m3.2xlarge")
  // Compute optimized
  case object c3_large   extends InstanceType("c3.large")
  case object c3_xlarge  extends InstanceType("c3.xlarge")
  case object c3_2xlarge extends InstanceType("c3.2xlarge")
  case object c3_4xlarge extends InstanceType("c3.4xlarge")
  case object c3_8xlarge extends InstanceType("c3.8xlarge")
  // Memory optimized
  case object r3_large   extends InstanceType("r3.large")
  case object r3_xlarge  extends InstanceType("r3.xlarge")
  case object r3_2xlarge extends InstanceType("r3.2xlarge")
  case object r3_4xlarge extends InstanceType("r3.4xlarge")
  case object r3_8xlarge extends InstanceType("r3.8xlarge")
  // Storage optimized
  case object i2_xlarge   extends InstanceType("i2.xlarge")
  case object i2_2xlarge  extends InstanceType("i2.2xlarge")
  case object i2_4xlarge  extends InstanceType("i2.4xlarge")
  case object i2_8xlarge  extends InstanceType("i2.8xlarge")
  case object hs1_8xlarge extends InstanceType("hs1.8xlarge")
  // GPU instances
  case object g2_2xlarge extends InstanceType("g2.2xlarge")

  // Previous Generation Instances //

  // General purpose
  case object m1_small  extends InstanceType("m1.small")
  case object m1_medium extends InstanceType("m1.medium")
  case object m1_large  extends InstanceType("m1.large")
  case object m1_xlarge extends InstanceType("m1.xlarge")
  // Compute optimized
  case object c1_medium   extends InstanceType("c1.medium")
  case object c1_xlarge   extends InstanceType("c1.xlarge")
  case object cc2_8xlarge extends InstanceType("cc2.8xlarge")
  // Memory optimized
  case object m2_xlarge   extends InstanceType("m2.xlarge")
  case object m2_2xlarge  extends InstanceType("m2.2xlarge")
  case object m2_4xlarge  extends InstanceType("m2.4xlarge")
  case object cr1_8xlarge extends InstanceType("cr1.8xlarge")
  // Storage optimized
  case object hi1_4xlarge extends InstanceType("hi1.4xlarge")
  // GPU instances
  case object cg1_4xlarge extends InstanceType("cg1.4xlarge")
  // Micro instances
  case object t1_micro extends InstanceType("t1.micro")

}
