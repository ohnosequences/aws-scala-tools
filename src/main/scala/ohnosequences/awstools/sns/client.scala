package ohnosequences.awstools.sns

import com.amazonaws.services.sns.model._
import com.amazonaws.services.sns.AmazonSNS
import scala.util.Try

case class ScalaSNSClient(val asJava: AmazonSNS) extends AnyVal { sns =>

  def getOrCreateTopic(name: String): Try[Topic] = Try {
    asJava.createTopic(name)
  }.map { response =>
    Topic(asJava, response.getTopicArn)
  }

  // NOTE: SNS topics are represented simply by their ARNs, so there is no way to get a topic by its name
  // NOTE: also creating or deleting a topic that doesn't exist does not result in an error

}
