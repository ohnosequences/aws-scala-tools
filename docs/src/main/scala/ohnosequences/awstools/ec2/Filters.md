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

import com.amazonaws.services.ec2

import scala.collection.JavaConversions._

case class Tag(name: String, value: String) {
  def toECTag = new ec2.model.Tag(name, value)
}

sealed abstract class Filter {
  def toEC2Filter: ec2.model.Filter
}

case class ResourceFilter(id: String) extends  Filter {
  override def toEC2Filter =  new ec2.model.Filter("resource-id:", List(id))
}

case class TagFilter(tag: Tag) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("tag:" + tag.name, List(tag.value))
}

//case class AutoScalingGroupFilter(groupName: String) extends Filter {
//  override def toEC2Filter = new ec2.model.Filter("aws:autoscaling:groupName", List(groupName))
//}

case class RequestStateFilter(states: String*) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("state", states)
}

case class InstanceStateFilter(states: String*) extends  Filter {
  override def toEC2Filter = new ec2.model.Filter("instance-state-name", states)
}



```

