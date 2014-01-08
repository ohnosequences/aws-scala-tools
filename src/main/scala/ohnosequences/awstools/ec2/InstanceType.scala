package ohnosequences.awstools.ec2


class InstanceType private () {
  override def toString = InstanceType.toNames(this)

  def toAWS = com.amazonaws.services.ec2.model.InstanceType.fromValue(toString)
}

object InstanceType {

  val toNames = scala.collection.mutable.HashMap[InstanceType, String]()
  val fromNames = scala.collection.mutable.HashMap[String, InstanceType]()

  def fromName(name: String): InstanceType = fromNames(name)



  def InstanceType(name: String): InstanceType = {
    val instanceType =  new InstanceType()
    toNames.put(instanceType, name)
    fromNames.put(name, instanceType)
    instanceType
  }

  val T1Micro = InstanceType("t1.micro")
  val M1Small = InstanceType("m1.small")
  val M1Medium = InstanceType("m1.medium")
  val M1Large = InstanceType("m1.large")
  val M1Xlarge = InstanceType("m1.xlarge")
  val M2Xlarge = InstanceType("m2.xlarge")
  val M22xlarge = InstanceType("m2.2xlarge")
  val M24xlarge = InstanceType("m2.4xlarge")
  val M3Xlarge = InstanceType("m3.xlarge")
  val M32xlarge = InstanceType("m3.2xlarge")
  val C1Medium = InstanceType("c1.medium")
  val C1Xlarge = InstanceType("c1.xlarge")
  val Hi14xlarge = InstanceType("hi1.4xlarge")
  val Cc14xlarge = InstanceType("cc1.4xlarge")
  val Cc28xlarge = InstanceType("cc2.8xlarge")
  val Cg14xlarge = InstanceType("cg1.4xlarge")
  val С3Large = InstanceType("c3.large")
  val С3XLarge = InstanceType("c3.xlarge")
  val С32XLarge = InstanceType("c3.2xlarge")
  val С34XLarge = InstanceType("c3.4xlarge")
  val С38XLarge = InstanceType("c3.8xlarge")

}










