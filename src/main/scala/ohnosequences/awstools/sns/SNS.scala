package ohnosequences.awstools.sns

import java.io.File

import ohnosequences.awstools.regions.Region._

import com.amazonaws.auth._
import com.amazonaws.services.sns.{AmazonSNSClient, AmazonSNS}
import com.amazonaws.services.sns.model.{CreateTopicRequest}
import com.amazonaws.internal.StaticCredentialsProvider

class SNS(val sns: AmazonSNS) {

  def createTopic(name: String) = {
    Topic(sns, sns.createTopic(new CreateTopicRequest(name)).getTopicArn, name)
  }

  def shutdown() {
    sns.shutdown()
  }

}

object SNS {

  def create(): SNS = {
    create(new InstanceProfileCredentialsProvider())
  }

  def create(credentials: AWSCredentialsProvider, region: ohnosequences.awstools.regions.Region = Ireland): SNS = {
    val snsClient = new AmazonSNSClient(credentials)
    snsClient.setRegion(region.toAWSRegion)
    new SNS(snsClient)
  }
}
