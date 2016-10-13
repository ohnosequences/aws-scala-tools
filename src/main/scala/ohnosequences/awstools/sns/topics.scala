package ohnosequences.awstools.sns

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

  // TODO: make it return a Stream making more requests only if needed
  def listAllSubscriptions: Seq[Subscription] = {

    @scala.annotation.tailrec
    def tokens_rec(response: ListSubscriptionsByTopicResult, acc: Seq[Subscription]): Seq[Subscription] = {
      val subs = response.getSubscriptions

      // NOTE: next token is null if there's nothing more to list
      Option(response.getNextToken) match {
        case Some(token) if (subs.nonEmpty) => tokens_rec(
          sns.listSubscriptionsByTopic(topic.arn, token),
          subs ++ acc
        )
        case _ => acc
      }
    }

    tokens_rec(sns.listSubscriptionsByTopic(topic.arn), Seq())
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
