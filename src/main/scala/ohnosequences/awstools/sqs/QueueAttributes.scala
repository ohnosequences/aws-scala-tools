//package ohnosequences.awstools.sqs
//
//
//class QueueAttributes private () {
//  override def toString = QueueAttributes.toNames(this)
//
//  def toAWS = com.amazonaws.services.ec2.model.InstanceType.fromValue(toString)
//}
//
//object QueueAttributes {
//
//  val toNames = scala.collection.mutable.HashMap[QueueAttributes, String]()
//  val fromNames = scala.collection.mutable.HashMap[String, QueueAttributes]()
//
//  def fromName(name: String): QueueAttributes = fromNames(name)
//
//
//
//  def QueueAttributes(name: String): QueueAttributes = {
//    val instanceType =  new QueueAttributes()
//    toNames.put(instanceType, name)
//    fromNames.put(name, instanceType)
//    instanceType
//  }
//
//  val T1Micro = QueueAttributes("t1.micro")
//}
