package ohnosequences.awstools.sns

import java.io.File

import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.sns.{AmazonSNSClient, AmazonSNS}
import com.amazonaws.services.sns.model.{DeleteTopicRequest, CreateTopicRequest}

class SNS(val sns: AmazonSNS) {

  def createTopic(name: String) = Topic(sns, sns.createTopic(new CreateTopicRequest(name)).getTopicArn, name)

  //def getTopic(name: String) = sns.

  def shutdown() {
    sns.shutdown()
  }

}

object SNS {
  def create(credentialsFile: File): SNS = {
    val snsClient = new AmazonSNSClient(new PropertiesCredentials(credentialsFile))
    snsClient.setEndpoint("http://sns.eu-west-1.amazonaws.com")
    new SNS(snsClient)
  }
}



