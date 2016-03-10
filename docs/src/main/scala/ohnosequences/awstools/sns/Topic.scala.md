
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




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md