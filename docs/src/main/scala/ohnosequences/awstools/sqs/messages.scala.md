
```scala
package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs, sqs.AmazonSQS, sqs.model._
import scala.collection.JavaConversions._
import scala.concurrent.duration._
import scala.util.Try
import java.net.URL
```

This is a wrapper for an instance of an SQS message. You can get it with `Queue#receiveMessage`.

```scala
case class Message(
  val queue: Queue,
  val asJava: sqs.model.Message
) { message =>
```

Unique message identifier. You get this when you send a message, but you can't use it to _refer_ to messages.

```scala
  def id: MessageId = asJava.getMessageId()
```

A handle you get for each instance of a _received_ message. You need it to delete a message or change its visibility timeout.

```scala
  def receiptHandle: String = asJava.getReceiptHandle()

  def body: String = asJava.getBody()
```

Note that to message will be deleted even if it's locked by the visibility timeout.

```scala
  def delete(): Try[Unit] = Try {
    queue.sqs.deleteMessage(
      queue.url.toString,
      message.receiptHandle
    )
  }
```

Note that the total visibility time for a message is 12 hours. See more details in the [Visibility Timeout](http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/AboutVT.html) Amazon docs.


```scala
  def changeVisibility(additionalSeconds: Integer): Try[Unit] = Try {
    queue.sqs.changeMessageVisibility(
      queue.url.toString,
      message.receiptHandle,
      additionalSeconds
    )
  }

  override def toString = Map(
    "id" -> message.id,
    // "receiptHandle" -> message.receiptHandle,
    "body" -> message.body
  ).toString
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
[main/scala/ohnosequences/awstools/sns/client.scala]: ../sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: ../sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: ../sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: ../sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../../test/scala/ohnosequences/awstools/sqs.scala.md