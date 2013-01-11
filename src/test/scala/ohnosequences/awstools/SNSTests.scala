package ohnosequences.awstools.sns

import org.junit.Test
import org.junit.Assert._

import com.amazonaws.auth.policy._
import com.amazonaws.auth.policy.Statement.Effect

import ohnosequences.awstools.sqs.SQS
import java.io.File
import com.amazonaws.auth.policy.actions.SQSActions
import com.amazonaws.auth.policy.conditions.{ConditionFactory, ArnCondition}



class SNSTests {

  @Test
  def policyTests {

    val sqs = SQS.create(new File("AwsCredentials.properties"))
    val queueName = "test_" + System.currentTimeMillis

    val queue = sqs.createQueue(queueName)
    val arn = "arn:aws:sqs:eu-west-1:393321850454:outputQueue"

    val policyId = "/SQSDefaultPolicy"
    val policy = new Policy(policyId).withStatements(new Statement(Effect.Allow)
      .withPrincipals(Principal.AllUsers)
      .withActions(SQSActions.SendMessage)
      .withResources(new Resource(arn))
      .withConditions(ConditionFactory.newSourceArnCondition(arn))
    )

    println(policy.toJson)

    queue.delete()
    sqs.shutdown()

    assertEquals(1, policy.getStatements.size())

  }


}

