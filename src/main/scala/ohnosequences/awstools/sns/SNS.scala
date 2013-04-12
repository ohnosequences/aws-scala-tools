package ohnosequences.awstools.sns

import java.io.File

import com.amazonaws.auth.{AWSCredentials, BasicAWSCredentials, PropertiesCredentials}
import com.amazonaws.services.sns.{AmazonSNSClient, AmazonSNS}
import com.amazonaws.services.sns.model.{ListTopicsRequest, DeleteTopicRequest, CreateTopicRequest}

class SNS(val sns: AmazonSNS) {

  def createTopic(name: String) = Topic(sns, sns.createTopic(new CreateTopicRequest(name)).getTopicArn, name)

//  def getTopic(name: String) = {
//    sns.listTopics(new ListTopicsRequest()
//      .
//    )
//  }

  def shutdown() {
    sns.shutdown()
  }

}

object SNS {

  def create(credentialsFile: File): SNS = {
    create(new PropertiesCredentials(credentialsFile))
  }

  def create(accessKey: String, secretKey: String): SNS = {
    create(new BasicAWSCredentials(accessKey, secretKey))
  }

  def create(credentials: AWSCredentials): SNS = {
    val snsClient = new AmazonSNSClient(credentials)
    snsClient.setEndpoint("http://sns.eu-west-1.amazonaws.com")
    new SNS(snsClient)
  }
}



