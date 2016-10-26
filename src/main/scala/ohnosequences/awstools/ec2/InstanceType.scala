package ohnosequences.awstools.ec2

import com.amazonaws.services.ec2.model

sealed trait AnyInstanceType {
  type Family <: InstanceType.Family
  val  family: Family

  val size: String

  final lazy val name: String = s"${family.prefix}.${size}"
  override def toString = name
}

case object AnyInstanceType {
  import InstanceType._

  type ofGeneration[G <: AnyGeneration] = AnyInstanceType { type Family <: G }
  type ofFamily[F <: Family] = AnyInstanceType { type Family = F }

  implicit def toJavaInstanceType(t: AnyInstanceType):
    model.InstanceType =
    model.InstanceType.fromValue(t.name)
}

sealed class InstanceType[
  F <: InstanceType.Family
](val family: F, val size: String) extends AnyInstanceType {
  type Family = F
}

case object InstanceType {

  // This is taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/instance-types.html
  // TODO: write tests that check that the string name corresponds to the object name

  sealed trait AnyGeneration
  trait CurrentGeneration extends AnyGeneration
  trait PreviousGeneration extends AnyGeneration

  sealed class Family(val prefix: String) { family: AnyGeneration => }

  /* ### Current Generation Instances */

  /* #### General purpose */
  case object t2 extends Family("t2") with CurrentGeneration {
    case object nano   extends InstanceType(t2, "nano")
    case object micro  extends InstanceType(t2, "micro")
    case object small  extends InstanceType(t2, "small")
    case object medium extends InstanceType(t2, "medium")
    case object large  extends InstanceType(t2, "large")
  }
  case object m4 extends Family("m4") with CurrentGeneration {
    case object large    extends InstanceType(m4, "large")
    case object xlarge   extends InstanceType(m4, "xlarge")
    case object x2large  extends InstanceType(m4, "2xlarge")
    case object x4large  extends InstanceType(m4, "4xlarge")
    case object x10large extends InstanceType(m4, "10xlarge")
    case object x16large extends InstanceType(m4, "16xlarge")
  }
  case object m3 extends Family("m3") with CurrentGeneration {
    case object medium  extends InstanceType(m3, "medium")
    case object large   extends InstanceType(m3, "large")
    case object xlarge  extends InstanceType(m3, "xlarge")
    case object x2large extends InstanceType(m3, "2xlarge")
  }

  /* #### Compute optimized */
  case object c4 extends Family("c4") with CurrentGeneration {
    case object large   extends InstanceType(c4, "large")
    case object xlarge  extends InstanceType(c4, "xlarge")
    case object x2large extends InstanceType(c4, "2xlarge")
    case object x4large extends InstanceType(c4, "4xlarge")
    case object x8large extends InstanceType(c4, "8xlarge")
  }
  case object c3 extends Family("c3") with CurrentGeneration {
    case object large   extends InstanceType(c3, "large")
    case object xlarge  extends InstanceType(c3, "xlarge")
    case object x2large extends InstanceType(c3, "2xlarge")
    case object x4large extends InstanceType(c3, "4xlarge")
    case object x8large extends InstanceType(c3, "8xlarge")
  }

  /* #### Memory optimized */
  case object r3 extends Family("r3") with CurrentGeneration {
    case object large   extends InstanceType(r3, "large")
    case object xlarge  extends InstanceType(r3, "xlarge")
    case object x2large extends InstanceType(r3, "2xlarge")
    case object x4large extends InstanceType(r3, "4xlarge")
    case object x8large extends InstanceType(r3, "8xlarge")
  }
  case object x1 extends Family("x1") with CurrentGeneration {
    case object x16large extends InstanceType(x1, "16xlarge")
    case object x32large extends InstanceType(x1, "32xlarge")
  }

  /* #### Storage optimized */
  case object i2 extends Family("i2") with CurrentGeneration {
    case object xlarge  extends InstanceType(i2, "xlarge")
    case object x2large extends InstanceType(i2, "2xlarge")
    case object x4large extends InstanceType(i2, "4xlarge")
    case object x8large extends InstanceType(i2, "8xlarge")
  }
  case object d2 extends Family("d2") with CurrentGeneration {
    case object xlarge  extends InstanceType(d2, "xlarge")
    case object x2large extends InstanceType(d2, "2xlarge")
    case object x4large extends InstanceType(d2, "4xlarge")
    case object x8large extends InstanceType(d2, "8xlarge")
  }

  /* #### Accelerated computing */
  case object p2 extends Family("p2") with CurrentGeneration {
    case object xlarge   extends InstanceType(p2, "xlarge")
    case object x8large  extends InstanceType(p2, "8xlarge")
    case object x16large extends InstanceType(p2, "16xlarge")
  }
  case object g2 extends Family("g2") with CurrentGeneration {
    case object x2large extends InstanceType(g2, "2xlarge")
    case object x8large extends InstanceType(g2, "8xlarge")
  }


  /* ### Previous Generation Instances */

  /* #### General purpose */
  case object m1 extends Family("m1") with PreviousGeneration {
    case object small  extends InstanceType(m1, "small")
    case object medium extends InstanceType(m1, "medium")
    case object large  extends InstanceType(m1, "large")
    case object xlarge extends InstanceType(m1, "xlarge")
  }

  /* #### Compute optimized */
  case object c1 extends Family("c1") with PreviousGeneration {
    case object medium  extends InstanceType(c1, "medium")
    case object xlarge  extends InstanceType(c1, "xlarge")
  }
  case object cc2 extends Family("cc2") with PreviousGeneration {
    case object x8large extends InstanceType(cc2, "8xlarge")
  }

  /* #### Memory optimized */
  case object m2 extends Family("m2") with PreviousGeneration {
    case object xlarge  extends InstanceType(m2, "xlarge")
    case object x2large extends InstanceType(m2, "2xlarge")
    case object x4large extends InstanceType(m2, "4xlarge")
  }
  case object cr1 extends Family("cr1") with PreviousGeneration {
    case object x8large extends InstanceType(cr1, "8xlarge")
  }

  /* #### Storage optimized */
  case object hi1 extends Family("hi1") with PreviousGeneration {
    case object x4large extends InstanceType(hi1, "4xlarge")
  }
  case object hs1 extends Family("hs1") with PreviousGeneration {
    case object x8large extends InstanceType(hs1, "8xlarge")
  }

  /* #### GPU instances */
  case object cg1 extends Family("cg1") with PreviousGeneration {
    case object x4large extends InstanceType(cg1, "4xlarge")
  }

  /* #### Micro instances */
  case object t1 extends Family("t1") with PreviousGeneration {
    case object micro extends InstanceType(t1, "micro")
  }

}
