
```scala
package ohnosequences.awstools.sns

import ohnosequences.awstools._
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model._
import com.amazonaws.auth.policy.{Resource, Principal, Statement, Policy}
import com.amazonaws.auth.policy.Statement.Effect
import com.amazonaws.auth.policy.actions.SQSActions
import com.amazonaws.auth.policy.conditions.ConditionFactory
import com.amazonaws.services.sqs.model.QueueAttributeName
import scala.collection.JavaConversions._
import scala.util.Try


case class Topic(
  val sns: AmazonSNS,
  val arn: String
) { topic =>

  def delete(): Try[Unit] = Try { sns.deleteTopic(topic.arn) }


  def publish(msg: String): Try[String] = Try {
    sns.publish(topic.arn, msg).getMessageId
  }

  def publish(msg: String, subject: String): Try[String] = Try {
    sns.publish(topic.arn, msg, subject).getMessageId
  }

  // TODO: publishJSON with dispatch by the subscriber protocol

  def listAllSubscriptions: Stream[Subscription] = {

    def fromResponse(response: ListSubscriptionsByTopicResult) = (
      // NOTE: next token is null if there's nothing more to list
      Option(response.getNextToken),
      response.getSubscriptions.toSeq
    )

    rotateTokens {
      case None        => fromResponse(sns.listSubscriptionsByTopic(topic.arn))
      case Some(token) => fromResponse(sns.listSubscriptionsByTopic(topic.arn, token))
    }
  }

  def subscribe(subscriber: Subscriber): Try[String] = Try {
    sns.subscribe(
      topic.arn,
      subscriber.protocol,
      subscriber.endpoint
    ).getSubscriptionArn
  }

  def subscribed(subscriber: Subscriber): Boolean = {

    listAllSubscriptions.exists { sub =>
      (sub.getProtocol == subscriber.protocol) &&
      (sub.getEndpoint == subscriber.endpoint)
    }
  }

  // def setAttribute(name: String, value: String) {
  //   sns.setTopicAttributes(new SetTopicAttributesRequest(topic.arn, name, value))
  // }

  override def toString = topic.arn
}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: ../autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: ../autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: ../autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ../ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ../ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ../ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: ../package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: ../regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: ../regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: ../sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: ../sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: ../sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: ../sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md