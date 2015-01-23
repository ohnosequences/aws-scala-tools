
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


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: ../dynamodb/DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: ../dynamodb/DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: ../dynamodb/Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: ../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: ../../../../../test/scala/ohnosequences/awstools/InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md