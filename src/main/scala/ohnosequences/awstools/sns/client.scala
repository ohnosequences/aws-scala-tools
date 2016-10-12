package ohnosequences.awstools.sns

import com.amazonaws.services.sns.model._
import com.amazonaws.services.sns.AmazonSNS
import scala.util.Try

case class ScalaSNSClient(val asJava: AmazonSNS) extends AnyVal { sns =>

  def getOrCreate(name: String): Try[Topic] = Try {
    asJava.createTopic(name)
  }.map { response =>
    Topic(asJava, response.getTopicArn)
  }

}
