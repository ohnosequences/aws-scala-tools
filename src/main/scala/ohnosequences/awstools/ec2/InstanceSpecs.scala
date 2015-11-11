package ohnosequences.awstools.ec2


trait AnyInstanceSpecs {

  type AMI <: AnyAMI
  val  ami: AMI

  type InstanceType <: AnyInstanceType
  val  instanceType: InstanceType

  // val supportsStorage: InstanceType SupportsStorageType AMI#Storage
  // val supportsVirt:    InstanceType SupportsVirtualization AMI#Virt
}

case class InstanceSpecs[
  A <: AnyLinuxAMI,
  T <: AnyInstanceType
](val ami: A,
  val instanceType: T
)(implicit
  val supportsStorage: T SupportsStorageType A#Storage,
  val supportsVirt:    T SupportsVirtualization A#Virt
) extends AnyInstanceSpecs {

  type AMI = A
  type InstanceType = T
}

// TODO: check it with this table: http://aws.amazon.com/amazon-linux-ami/instance-type-matrix/

// The list can be retrieved from http://www.ec2instances.info/?min_storage=1
@annotation.implicitNotFound( msg = """
Instance type

  ${T}

doesn't support storage type of the chosen AMI

  ${S}
""")
trait SupportsStorageType[T <: AnyInstanceType, S <: AnyStorageType]
case object SupportsStorageType {
  import InstanceType._

  implicit def ebs[T <: AnyInstanceType]:
      (T SupportsStorageType EBS.type) =
  new (T SupportsStorageType EBS.type) {}

  implicit def is_m3[T <: AnyInstanceType.ofFamily[m3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_r3[T <: AnyInstanceType.ofFamily[r3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_m1[T <: AnyInstanceType.ofFamily[m1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_m2[T <: AnyInstanceType.ofFamily[m2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_i2[T <: AnyInstanceType.ofFamily[i2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_d2[T <: AnyInstanceType.ofFamily[d2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cr1[T <: AnyInstanceType.ofFamily[cr1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cg1[T <: AnyInstanceType.ofFamily[cg1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_cc2[T <: AnyInstanceType.ofFamily[cc2.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_c3[T <: AnyInstanceType.ofFamily[c3.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  implicit def is_c1[T <: AnyInstanceType.ofFamily[c1.type]]:
      (T SupportsStorageType InstanceStore.type) =
  new (T SupportsStorageType InstanceStore.type) {}

  // TODO: what's g2 instances?
}


@annotation.implicitNotFound( msg = """
Instance type

  ${T}

doesn't support virtualization of the chosen AMI

  ${V}
""")
sealed trait SupportsVirtualization[T <: AnyInstanceType, V <: AnyVirtualization]
case object SupportsVirtualization {
  import InstanceType._

  /* All current generation instance types support HVM AMIs.
     The CC2, CR1, HI1, and HS1 previous generation instance types support HVM AMIs. */
  implicit def hvm_CurrentGeneration[T <: AnyInstanceType.ofGeneration[CurrentGeneration]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_cc2[T <: AnyInstanceType.ofFamily[cc2.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_cr1[T <: AnyInstanceType.ofFamily[cr1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  implicit def hvm_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsVirtualization HVM.type) =
  new (T SupportsVirtualization HVM.type) {}

  /* The C3 and M3 current generation instance types support PV AMIs.
     The C1, HI1, HS1, M1, M2, and T1 previous generation instance types support PV AMIs. */
  implicit def pv_c3[T <: AnyInstanceType.ofFamily[c3.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m3[T <: AnyInstanceType.ofFamily[m3.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_c1[T <: AnyInstanceType.ofFamily[c1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_hi1[T <: AnyInstanceType.ofFamily[hi1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_hs1[T <: AnyInstanceType.ofFamily[hs1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m1[T <: AnyInstanceType.ofFamily[m1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_m2[T <: AnyInstanceType.ofFamily[m2.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}

  implicit def pv_t1[T <: AnyInstanceType.ofFamily[t1.type]]:
      (T SupportsVirtualization PV.type) =
  new (T SupportsVirtualization PV.type) {}
}
