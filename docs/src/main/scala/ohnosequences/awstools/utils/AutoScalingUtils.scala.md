
```scala
package ohnosequences.awstools.utils

import com.amazonaws.services.autoscaling.AmazonAutoScaling
import com.amazonaws.services.autoscaling.model.{Instance, DescribeAutoScalingGroupsRequest}

import scala.collection.JavaConversions._

import scala.util.Try


/**
 * Created by Evdokim on 18.06.2015.
 */
object AutoScalingUtils {
  def describeInstances(as: AmazonAutoScaling,
                        groupName: String,
                        lastToken: Option[String],
                        limit: Option[Int]): Try[(Option[String], List[Instance])] = {
    Try {
      val request = new DescribeAutoScalingGroupsRequest()
        .withAutoScalingGroupNames(groupName)
      limit.foreach { l => request.setMaxRecords(l) }
      lastToken.foreach { t => request.setNextToken(t) }
      val r = as.describeAutoScalingGroups(request)
      (Option(r.getNextToken), r.getAutoScalingGroups.flatMap { _.getInstances}.toList)
    }

  }

}

```




[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../logging/S3Logger.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../sns/Topic.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: SQSUtils.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md