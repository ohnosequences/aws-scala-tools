
```scala
package ohnosequences.awstools.sqs

import com.amazonaws.services.sqs.model.{ Message => AmazonMessage, _ }
import com.amazonaws.services.sqs.AmazonSQS
import scala.concurrent._, duration._
import scala.collection.JavaConversions._
import java.net.URL
import scala.util.{ Try, Success, Failure }
```

Amazon Java SDK doesn't have an explicit abstraction for SQS queues. This API provides some convenience methods that are just wrappers for the Java SDK ones, mostly for the same of bettern return types, and some methods that have some more involved implementation, such as `sendBatch` and `poll`.


```scala
case class Queue(
  val sqs: AmazonSQS,
  val url: URL
) { queue =>

  // This shouldn't change over time, so I make it a lazy val:
  lazy val arn: String = getAttribute(QueueAttributeName.QueueArn)


  def delete(): Try[Unit] = Try { sqs.deleteQueue(queue.url.toString) }
```

Note, that you have to wait 60s after purging a queue until you can do it again.

```scala
  def purge():  Try[Unit] = Try { sqs.purgeQueue(new PurgeQueueRequest(queue.url.toString)) }
```

Sending just one message

```scala
  def sendOne(msg: String): Try[MessageId] = Try {
    sqs.sendMessage(queue.url.toString, msg).getMessageId
  }
```

This method tries to get just one message. It returns `Success(None)` if the queue is empty (at this particular moment).

```scala
  def receiveOne: Try[Option[Message]] = Try {

    val response: ReceiveMessageResult = sqs.receiveMessage(queue.url.toString)
    response.getMessages.headOption.map { Message(queue, _) }
  }
```

Sending messages in batches and in parallel. This method doesn't have the limitation of maximum 10 messages.

```scala
  def sendBatch(msgs: Iterator[String])(implicit ec: ExecutionContext): Future[SendBatchResult] = {

    // NOTE: maximum 10 messages at a time
    def sendGroup(group: Seq[String]): SendBatchResult = {
      val batch = group.zipWithIndex.map { case (msg, ix) =>
        new SendMessageBatchRequestEntry(ix.toString, msg)
      }
      val response: SendMessageBatchResult = sqs.sendMessageBatch(queue.url.toString, batch)

      SendBatchResult(
        response.getSuccessful.map { _.getMessageId },
        response.getFailed.map { err => (group.apply(err.getId.toInt), err) }
      )
    }

    Future.reduce(
      // TODO: check messages lenghts not to exceed the total batch size limit
      msgs.grouped(10).map { grp => Future { sendGroup(grp) } }
    ){ _ ++ _ }
  }
```

This method performs queue polling with various configurable parameters. It makes iterative receive message calls, until one of the conditions is met:
    - maximum messages received (`amountLimit`)
    - it gets 5 empty responses in a row (this is useful for short-polling, because it eventually returns empty responses)
    - timeout is met (`timeout`)
    - maximum number of messages in flight is reached (see [`OverLimitException`](http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/sqs/model/OverLimitException.html))

    Note that you can do either "short" or "long" polling by regulating the `adjustRequest` parameter. See [Amazon documentation](http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-long-polling.html) for more details.

    Note, that polling is quite a slow process, so don't use it for "just getting all messages" from a queue.


```scala
  def poll(
    timeout: Duration        = 10.seconds,
    amountLimit: Option[Int] = None,
    adjustRequest: ReceiveMessageRequest => ReceiveMessageRequest = identity
  ): Try[Seq[Message]] = {

    val start = Deadline.now
    def timePassed = -start.timeLeft
    def deadlineHasCome: Boolean = timeout < timePassed

    def wrapResult(msgs: Iterable[AmazonMessage]): Success[Seq[Message]] = Success(
      msgs.toSeq.map { Message(queue, _) }
    )

    val request = adjustRequest(new ReceiveMessageRequest(queue.url.toString))

    @scala.annotation.tailrec
    def poll_rec(
      acc: scala.collection.mutable.Map[MessageId, AmazonMessage],
      emptyResponses: Int
    ): Try[Seq[Message]] = {

      val maxMessagesForNextRequest: Int = amountLimit.map { max =>
        // Always not more than 10
        math.min(10, max - acc.size)
        // And if there's no maximum we want 10 messages every time
      }.getOrElse(10)

      if (deadlineHasCome || maxMessagesForNextRequest <= 0) wrapResult(acc.values)
      else {
        val response: Try[ Seq[(MessageId, AmazonMessage)] ] =
          Try {
            sqs.receiveMessage(
              request.withMaxNumberOfMessages(maxMessagesForNextRequest)
            ).getMessages.map { msg =>
              msg.getMessageId -> msg
            }
          }.recover {
            // "ReceiveMessage returns this error if the maximum number of messages inflight has already been reached"
            case e: OverLimitException => Seq()
          }

        response match {
          case Failure(ex) => Failure(ex) // here we change X in Failure[X]
          case Success(msgs) => {

            if (msgs.isEmpty) {
              // If we receive over 5 empty responses in a row, there's unlikely anything else left in the queue
              if (emptyResponses > 5) wrapResult(acc.values)
              else poll_rec(acc ++= msgs, emptyResponses + 1)
            } else
              poll_rec(acc ++= msgs, 0)
          }
        }
      }
    }

    poll_rec(scala.collection.mutable.Map(), 0)
  }
```

Providing read access to some useful attributes

```scala
  def getAttribute(attr: QueueAttributeName): String = {
    // NOTE: the only exception it throws is about invalid name of the attribute, but we control it by using the QueueAttributeName enum instead of just String.
    sqs.getQueueAttributes(queue.url.toString, List(attr.toString))
      .getAttributes.get(attr.toString)
  }
```

Note that these attributes return **approximate** numbers, meaning that you cannot reliably use them for determining the number of messages in a queue.

```scala
  def approxMsgAvailable: Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessages).toInt
  def approxMsgInFlight:  Int = getAttribute(QueueAttributeName.ApproximateNumberOfMessagesNotVisible).toInt

  // NOTE: this is actually limited by Int, but we use FiniteDuration for convenience
  def visibilityTimeout: FiniteDuration =
    getAttribute(QueueAttributeName.VisibilityTimeout).toInt.seconds
```

A shortcut for setting attributes

```scala
  def setAttribute(attr: QueueAttributeName, value: String): Try[Unit] = Try {
    sqs.setQueueAttributes(queue.url.toString, Map(attr.toString -> value))
  }
```

Note that visibility timeout cannot be more than 12 hours

```scala
  def setVisibilityTimeout(time: FiniteDuration): Try[Unit] =
    setAttribute(QueueAttributeName.VisibilityTimeout, time.toSeconds.toString)

  // TODO: get/set for MessageRetentionPeriod, ReceiveMessageWaitTimeSeconds, etc.


  override def toString: String = url.toString
}
```

When sending messages in batch, some may fail, so the result will be a set of IDs for successfully sent and a set of those that failed together with the information about the reason (see Amazon documentation for BatchResultErrorEntry)

```scala
case class SendBatchResult(
  val sent: Seq[MessageId],
  val failures: Seq[(String, BatchResultErrorEntry)]
) {

  def ++(other: SendBatchResult): SendBatchResult = SendBatchResult(
    other.sent ++ this.sent,
    other.failures ++ this.failures
  )
}

case object SendBatchResult {

  val empty: SendBatchResult = SendBatchResult(Seq(), Seq())
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