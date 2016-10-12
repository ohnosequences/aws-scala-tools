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
import scala.util.Try

case class Topic(
  sns: AmazonSNS,
  arn: String
) { topic =>

  def delete(): Try[Unit] = Try { sns.deleteTopic(topic.arn) }


  def publish(msg: String): Try[String] = Try {
    sns.publish(topic.arn, msg).getMessageId
  }

  def publish(msg: String, subject: String): Try[String] = Try {
    sns.publish(topic.arn, msg, subject).getMessageId
  }

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

  def subscribeQueue(queue: Queue): Unit = {

    sns.subscribe(new SubscribeRequest(topic.arn, "sqs", queue.arn))

    val policyId = queue.arn + "\\SQSDefaultPolicy"

    val policy = new Policy(policyId).withStatements(new Statement(Effect.Allow)
      .withPrincipals(Principal.AllUsers)
      .withActions(SQSActions.SendMessage)
      .withResources(new Resource(queue.arn))
      .withConditions(ConditionFactory.newSourceArnCondition(topic.arn))
    )

    queue.setAttribute(QueueAttributeName.Policy, policy.toJson)
  }

  def subscribeEmail(email: String): Unit = {
    sns.subscribe(new SubscribeRequest()
      .withTopicArn(topic.arn)
      .withProtocol("email")
      .withEndpoint(email)
    )
  }

  def isEmailSubscribed(email: String): Boolean = {

    listAllSubscriptions.exists { sub =>
      sub.getProtocol.equals("email") &&
      sub.getEndpoint.equals(email)
    }
  }

  // def setAttribute(name: String, value: String) {
  //   sns.setTopicAttributes(new SetTopicAttributesRequest(topic.arn, name, value))
  // }

  override def toString = topic.arn
}
