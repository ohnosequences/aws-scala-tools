### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](EC2.md)
            + [Filters.scala](Filters.md)
            + [InstanceType.scala](InstanceType.md)
            + [Utils.scala](Utils.md)
          + s3
            + [Bucket.scala](../s3/Bucket.md)
            + [S3.scala](../s3/S3.md)
          + sns
            + [SNS.scala](../sns/SNS.md)
            + [Topic.scala](../sns/Topic.md)
          + sqs
            + [Queue.scala](../sqs/Queue.md)
            + [SQS.scala](../sqs/SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.md)
          + [EC2Tests.scala](../../../../../test/scala/ohnosequences/awstools/EC2Tests.md)
          + [S3Tests.scala](../../../../../test/scala/ohnosequences/awstools/S3Tests.md)
          + [SNSTests.scala](../../../../../test/scala/ohnosequences/awstools/SNSTests.md)
          + [SQSTests.scala](../../../../../test/scala/ohnosequences/awstools/SQSTests.md)

------


```scala
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

}











```

