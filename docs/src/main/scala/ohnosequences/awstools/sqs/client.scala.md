
```scala
package ohnosequences.awstools.sqs

import java.io.File
import java.net.URL

import scala.util.Try
import scala.collection.JavaConversions._

import ohnosequences.awstools.regions._

import com.amazonaws.services.sqs._
import com.amazonaws.services.sqs.model._
import com.amazonaws.auth._
import com.amazonaws.AmazonServiceException
import com.amazonaws.internal.StaticCredentialsProvider


case class ScalaSQSClient(val asJava: AmazonSQS) extends AnyVal { sqs =>
```

This may fail if the queue with this name was recently deleted (within 60s)

```scala
  def getOrCreateQueue(queueName: String): Try[Queue] = Try {
    val response: CreateQueueResult = sqs.asJava.createQueue(queueName)
    Queue(sqs.asJava, new URL(response.getQueueUrl))
  }
```

This may fail if the queue does not exist

```scala
  def getQueue(queueName: String): Try[Queue] = Try {
    val response: GetQueueUrlResult = sqs.asJava.getQueueUrl(queueName)
    Queue(sqs.asJava, new URL(response.getQueueUrl))
  }

  def listQueues(namePrefix: String): Try[Seq[Queue]] = Try {
    val response: ListQueuesResult = sqs.asJava.listQueues(namePrefix)
    response.getQueueUrls.map { url => Queue(sqs.asJava, new URL(url)) }
  }
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