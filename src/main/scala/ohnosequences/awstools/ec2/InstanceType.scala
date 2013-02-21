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
  val C1Medium = InstanceType("c1.medium")
  val M1Small = InstanceType("m1.small")
  val M1Medium = InstanceType("m1.medium")

}










