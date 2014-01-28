package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model.{InstanceType => JavaInstanceType}

sealed class InstanceType(name: String) {
  override def toString = name

  @deprecated("There is an implicit conversion for that in ohnosequences.awstools.ec2.InstanceType, just import it",
              since = "v0.6.0")
  def toAWS = JavaInstanceType.fromValue(name)
}

object InstanceType {

  // Let's give them more readable names
  case object t1_micro    extends InstanceType("t1.micro")
  case object m1_small    extends InstanceType("m1.small")
  case object m1_medium   extends InstanceType("m1.medium")
  case object m1_large    extends InstanceType("m1.large")
  case object m1_xlarge   extends InstanceType("m1.xlarge")
  case object m3_xlarge   extends InstanceType("m3.xlarge")
  case object m3_2xlarge  extends InstanceType("m3.2xlarge")
  case object m2_xlarge   extends InstanceType("m2.xlarge")
  case object m2_2xlarge  extends InstanceType("m2.2xlarge")
  case object m2_4xlarge  extends InstanceType("m2.4xlarge")
  case object cr1_8xlarge extends InstanceType("cr1.8xlarge")
  case object i2_xlarge   extends InstanceType("i2.xlarge")
  case object i2_2xlarge  extends InstanceType("i2.2xlarge")
  case object i2_4xlarge  extends InstanceType("i2.4xlarge")
  case object i2_8xlarge  extends InstanceType("i2.8xlarge")
  case object hi1_4xlarge extends InstanceType("hi1.4xlarge")
  case object hs1_8xlarge extends InstanceType("hs1.8xlarge")
  case object c1_medium   extends InstanceType("c1.medium")
  case object c1_xlarge   extends InstanceType("c1.xlarge")
  case object c3_large    extends InstanceType("c3.large")
  case object c3_xlarge   extends InstanceType("c3.xlarge")
  case object c3_2xlarge  extends InstanceType("c3.2xlarge")
  case object c3_4xlarge  extends InstanceType("c3.4xlarge")
  case object c3_8xlarge  extends InstanceType("c3.8xlarge")
  case object cc1_4xlarge extends InstanceType("cc1.4xlarge")
  case object cc2_8xlarge extends InstanceType("cc2.8xlarge")
  case object g2_2xlarge  extends InstanceType("g2.2xlarge")
  case object cg1_4xlarge extends InstanceType("cg1.4xlarge")

  implicit def toJavaInstanceType(t: InstanceType): JavaInstanceType = 
    JavaInstanceType.fromValue(t.name)

  // only for back compatibility:
  @deprecated("Use conversion from an arbitrary String carefully", since = "v0.6.0")
  def fromName(name: String): InstanceType = new InstanceType(name)

  @deprecated("Use t1_micro instead",    "v0.6.0") val T1Micro    = t1_micro
  @deprecated("Use m1_small instead",    "v0.6.0") val M1Small    = m1_small
  @deprecated("Use m1_medium instead",   "v0.6.0") val M1Medium   = m1_medium
  @deprecated("Use m1_large instead",    "v0.6.0") val M1Large    = m1_large
  @deprecated("Use m1_xlarge instead",   "v0.6.0") val M1Xlarge   = m1_xlarge
  @deprecated("Use m3_xlarge instead",   "v0.6.0") val M3Xlarge   = m3_xlarge
  @deprecated("Use m3_2xlarge instead",  "v0.6.0") val M32xlarge  = m3_2xlarge
  @deprecated("Use m2_xlarge instead",   "v0.6.0") val M2Xlarge   = m2_xlarge
  @deprecated("Use m2_2xlarge instead",  "v0.6.0") val M22xlarge  = m2_2xlarge
  @deprecated("Use m2_4xlarge instead",  "v0.6.0") val M24xlarge  = m2_4xlarge
  @deprecated("Use cr1_8xlarge instead", "v0.6.0") val Cr18xlarge = cr1_8xlarge
  @deprecated("Use i2_xlarge instead",   "v0.6.0") val I2Xlarge   = i2_xlarge
  @deprecated("Use i2_2xlarge instead",  "v0.6.0") val I22xlarge  = i2_2xlarge
  @deprecated("Use i2_4xlarge instead",  "v0.6.0") val I24xlarge  = i2_4xlarge
  @deprecated("Use i2_8xlarge instead",  "v0.6.0") val I28xlarge  = i2_8xlarge
  @deprecated("Use hi1_4xlarge instead", "v0.6.0") val Hi14xlarge = hi1_4xlarge
  @deprecated("Use hs1_8xlarge instead", "v0.6.0") val Hs18xlarge = hs1_8xlarge
  @deprecated("Use c1_medium instead",   "v0.6.0") val C1Medium   = c1_medium
  @deprecated("Use c1_xlarge instead",   "v0.6.0") val C1Xlarge   = c1_xlarge
  @deprecated("Use c3_large instead",    "v0.6.0") val C3Large    = c3_large
  @deprecated("Use c3_xlarge instead",   "v0.6.0") val C3Xlarge   = c3_xlarge
  @deprecated("Use c3_2xlarge instead",  "v0.6.0") val C32xlarge  = c3_2xlarge
  @deprecated("Use c3_4xlarge instead",  "v0.6.0") val C34xlarge  = c3_4xlarge
  @deprecated("Use c3_8xlarge instead",  "v0.6.0") val C38xlarge  = c3_8xlarge
  @deprecated("Use cc1_4xlarge instead", "v0.6.0") val Cc14xlarge = cc1_4xlarge
  @deprecated("Use cc2_8xlarge instead", "v0.6.0") val Cc28xlarge = cc2_8xlarge
  @deprecated("Use g2_2xlarge instead",  "v0.6.0") val G22xlarge  = g2_2xlarge
  @deprecated("Use cg1_4xlarge instead", "v0.6.0") val Cg14xlarge = cg1_4xlarge

}










