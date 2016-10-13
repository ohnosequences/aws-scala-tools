package ohnosequences.awstools.sns

import ohnosequences.awstools.sqs.Queue
import java.net.URI


sealed abstract class Subscriber private[awstools](
  val protocol: String,
  val endpoint: String
)

case object Subscriber {

  // delivery of JSON-encoded message via HTTP POST
  case class http(uri: URI)  extends Subscriber("http", uri.normalize.toString)
  // delivery of JSON-encoded message via HTTPS POST
  case class https(uri: URI) extends Subscriber("https", uri.normalize.toString)
  // delivery of message via SMTP
  case class email(addr: String)      extends Subscriber("email", addr)
  // delivery of JSON-encoded message via SMTP
  case class email_json(addr: String) extends Subscriber("email-json", addr)
  // delivery of message via SMS
  case class sms(phone: Long) extends Subscriber("sms", math.abs(phone).toString)
  // delivery of JSON-encoded message to an Amazon SQS queue
  case class sqs(queue: Queue) extends Subscriber("sqs", queue.arn)

  // TODO:
  // delivery of JSON-encoded message to an EndpointArn for a mobile app and device.
  // case class application()
  // delivery of JSON-encoded message to an AWS Lambda function.
  // case class lambda(lamdba: Lambda)
}
