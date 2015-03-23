
```scala
package ohnosequences.awstools.sns

import ohnosequences.awstools.sqs.Queue

import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model._
import com.amazonaws.auth.policy.{Resource, Principal, Statement, Policy}
import com.amazonaws.auth.policy.Statement.Effect
import com.amazonaws.auth.policy.actions.SQSActions
import com.amazonaws.auth.policy.conditions.ConditionFactory
import com.amazonaws.services.sqs.model.QueueAttributeName
import scala.collection.JavaConversions._

case class Topic(sns: AmazonSNS, topicArn: String, name: String) {

  def publish(message: String) {
    sns.publish(new PublishRequest(topicArn, message))
  }

  def publish(message: String, subject: String) {
    sns.publish(new PublishRequest(topicArn, message, subject))
  }

  def setAttribute(name: String, value: String) {
    sns.setTopicAttributes(new SetTopicAttributesRequest(topicArn, name, value))
  }

  def subscribeQueue(queue: Queue) {

    sns.subscribe(new SubscribeRequest(topicArn, "sqs", queue.getArn))

    val policyId = queue.getArn + "\\SQSDefaultPolicy"

    val policy = new Policy(policyId).withStatements(new Statement(Effect.Allow)
      .withPrincipals(Principal.AllUsers)
      .withActions(SQSActions.SendMessage)
      .withResources(new Resource(queue.getArn))
      .withConditions(ConditionFactory.newSourceArnCondition(topicArn))
    )

    queue.setAttributes(Map(QueueAttributeName.Policy.toString -> policy.toJson))

  }

  def isEmailSubscribed(email: String): Boolean = {

    val subscriptions = getSubscriptions()
    //println(subscriptions)
    val subscribed = subscriptions.exists { subscription =>
      subscription.getProtocol.equals("email") && subscription.getEndpoint.equals(email)
    }
    //println("subscribed = " + subscribed)
    subscribed

  }

  //todo next token!
  def getSubscriptions(): List[Subscription] = {

    sns.listSubscriptionsByTopic(new ListSubscriptionsByTopicRequest()
      .withTopicArn(topicArn)
    ).getSubscriptions.toList
  }

  def subscribeEmail(email: String) = {
    sns.subscribe(new SubscribeRequest()
      .withTopicArn(topicArn)
      .withProtocol("email")
      .withEndpoint(email)
    )
  }

  override def toString = {
    "[ name=" + name + "; " + "arn=" + topicArn + " (" + sns.listSubscriptions() + ") ]"
  //  sns.listSubscriptions()
  }

  def delete() {
    try {
      sns.deleteTopic(new DeleteTopicRequest(topicArn))
    } catch {
      case t: Throwable => println("error during topic deletion " + topicArn + " : " +  t.getMessage); t.printStackTrace()
    }
  }
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
            + [AutoScaling.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]
            + [AutoScalingGroup.scala][main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]
          + [AWSClients.scala][main\scala\ohnosequences\awstools\AWSClients.scala]
          + dynamodb
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]
          + ec2
            + [EC2.scala][main\scala\ohnosequences\awstools\ec2\EC2.scala]
            + [Filters.scala][main\scala\ohnosequences\awstools\ec2\Filters.scala]
            + [InstanceType.scala][main\scala\ohnosequences\awstools\ec2\InstanceType.scala]
            + [Utils.scala][main\scala\ohnosequences\awstools\ec2\Utils.scala]
          + regions
            + [Region.scala][main\scala\ohnosequences\awstools\regions\Region.scala]
          + s3
            + [Bucket.scala][main\scala\ohnosequences\awstools\s3\Bucket.scala]
            + [S3.scala][main\scala\ohnosequences\awstools\s3\S3.scala]
          + sns
            + [SNS.scala][main\scala\ohnosequences\awstools\sns\SNS.scala]
            + [Topic.scala][main\scala\ohnosequences\awstools\sns\Topic.scala]
          + sqs
            + [Queue.scala][main\scala\ohnosequences\awstools\sqs\Queue.scala]
            + [SQS.scala][main\scala\ohnosequences\awstools\sqs\SQS.scala]
          + utils
            + [DynamoDBUtils.scala][main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]
            + [SQSUtils.scala][main\scala\ohnosequences\awstools\utils\SQSUtils.scala]
        + benchmark
          + [Benchmark.scala][main\scala\ohnosequences\benchmark\Benchmark.scala]
        + logging
          + [Logger.scala][main\scala\ohnosequences\logging\Logger.scala]
          + [S3Logger.scala][main\scala\ohnosequences\logging\S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [EC2Tests.scala][test\scala\ohnosequences\awstools\EC2Tests.scala]
          + [InstanceTypeTests.scala][test\scala\ohnosequences\awstools\InstanceTypeTests.scala]
          + [RegionTests.scala][test\scala\ohnosequences\awstools\RegionTests.scala]
          + [S3Tests.scala][test\scala\ohnosequences\awstools\S3Tests.scala]
          + [SQSTests.scala][test\scala\ohnosequences\awstools\SQSTests.scala]
          + [TestCredentials.scala][test\scala\ohnosequences\awstools\TestCredentials.scala]

[main\scala\ohnosequences\awstools\autoscaling\AutoScaling.scala]: ..\autoscaling\AutoScaling.scala.md
[main\scala\ohnosequences\awstools\autoscaling\AutoScalingGroup.scala]: ..\autoscaling\AutoScalingGroup.scala.md
[main\scala\ohnosequences\awstools\AWSClients.scala]: ..\AWSClients.scala.md
[main\scala\ohnosequences\awstools\dynamodb\DynamoDBUtils.scala]: ..\dynamodb\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\ec2\EC2.scala]: ..\ec2\EC2.scala.md
[main\scala\ohnosequences\awstools\ec2\Filters.scala]: ..\ec2\Filters.scala.md
[main\scala\ohnosequences\awstools\ec2\InstanceType.scala]: ..\ec2\InstanceType.scala.md
[main\scala\ohnosequences\awstools\ec2\Utils.scala]: ..\ec2\Utils.scala.md
[main\scala\ohnosequences\awstools\regions\Region.scala]: ..\regions\Region.scala.md
[main\scala\ohnosequences\awstools\s3\Bucket.scala]: ..\s3\Bucket.scala.md
[main\scala\ohnosequences\awstools\s3\S3.scala]: ..\s3\S3.scala.md
[main\scala\ohnosequences\awstools\sns\SNS.scala]: SNS.scala.md
[main\scala\ohnosequences\awstools\sns\Topic.scala]: Topic.scala.md
[main\scala\ohnosequences\awstools\sqs\Queue.scala]: ..\sqs\Queue.scala.md
[main\scala\ohnosequences\awstools\sqs\SQS.scala]: ..\sqs\SQS.scala.md
[main\scala\ohnosequences\awstools\utils\DynamoDBUtils.scala]: ..\utils\DynamoDBUtils.scala.md
[main\scala\ohnosequences\awstools\utils\SQSUtils.scala]: ..\utils\SQSUtils.scala.md
[main\scala\ohnosequences\benchmark\Benchmark.scala]: ..\..\benchmark\Benchmark.scala.md
[main\scala\ohnosequences\logging\Logger.scala]: ..\..\logging\Logger.scala.md
[main\scala\ohnosequences\logging\S3Logger.scala]: ..\..\logging\S3Logger.scala.md
[test\scala\ohnosequences\awstools\EC2Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\EC2Tests.scala.md
[test\scala\ohnosequences\awstools\InstanceTypeTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\InstanceTypeTests.scala.md
[test\scala\ohnosequences\awstools\RegionTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\RegionTests.scala.md
[test\scala\ohnosequences\awstools\S3Tests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\S3Tests.scala.md
[test\scala\ohnosequences\awstools\SQSTests.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\SQSTests.scala.md
[test\scala\ohnosequences\awstools\TestCredentials.scala]: ..\..\..\..\..\test\scala\ohnosequences\awstools\TestCredentials.scala.md