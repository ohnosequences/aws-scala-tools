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
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
          + s3
            + [Bucket.scala](../s3/Bucket.md)
            + [S3.scala](../s3/S3.md)
          + sns
            + [SNS.scala](SNS.md)
            + [Topic.scala](Topic.md)
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

