
```scala
package ohnosequences

package object awstools {

  type Token = String

  def rotateTokens[Result](
    makeRequest: Option[Token] => (Option[Token], Seq[Result])
  ): Stream[Result] = {

    def rotateTokens_rec(token: Option[Token]): Stream[Result] = {

      if (token.isEmpty) Stream.empty
      else {
        val (nextToken, chunk) = makeRequest(token)
        chunk.toStream #::: rotateTokens_rec(nextToken)
      }
    }

    val (firstToken, firstChunk) = makeRequest(None)
    firstChunk.toStream #::: rotateTokens_rec(firstToken)
  }

  // def rotate[
  //   Request <: AmazonWebServiceRequest,
  //   Response <: AmazonWebServiceResult[ResponseMetadata],
  //   Result
  // ](baseRequest: Request,
  //   makeRequest: Request => Response
  // )(withToken: Request => (Token => Request),
  //   getNextToken: Request => Token,
  //   getResults: Response => Seq[Result]
  // ): Stream[Result] = {
  //
  //   def fromResponse(response: Response) = (
  //     // NOTE: next token is null if there's nothing more to list
  //     Option(getNextToken(response)),
  //     getResults(response)
  //   )
  //
  //   rotateTokens { token =>
  //     fromResponse(makeRequest(
  //       token.fold(baseRequest)(withToken(baseRequest))
  //     ))
  //   }
  // }
}

```




[main/scala/ohnosequences/awstools/autoscaling/client.scala]: autoscaling/client.scala.md
[main/scala/ohnosequences/awstools/autoscaling/filters.scala]: autoscaling/filters.scala.md
[main/scala/ohnosequences/awstools/autoscaling/package.scala]: autoscaling/package.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/client.scala]: ec2/client.scala.md
[main/scala/ohnosequences/awstools/ec2/instances.scala]: ec2/instances.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType-AMI.scala]: ec2/InstanceType-AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ec2/package.scala.md
[main/scala/ohnosequences/awstools/package.scala]: package.scala.md
[main/scala/ohnosequences/awstools/regions/aliases.scala]: regions/aliases.scala.md
[main/scala/ohnosequences/awstools/regions/package.scala]: regions/package.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/client.scala]: sns/client.scala.md
[main/scala/ohnosequences/awstools/sns/package.scala]: sns/package.scala.md
[main/scala/ohnosequences/awstools/sns/subscribers.scala]: sns/subscribers.scala.md
[main/scala/ohnosequences/awstools/sns/topics.scala]: sns/topics.scala.md
[main/scala/ohnosequences/awstools/sqs/client.scala]: sqs/client.scala.md
[main/scala/ohnosequences/awstools/sqs/messages.scala]: sqs/messages.scala.md
[main/scala/ohnosequences/awstools/sqs/package.scala]: sqs/package.scala.md
[main/scala/ohnosequences/awstools/sqs/queues.scala]: sqs/queues.scala.md
[test/scala/ohnosequences/awstools/autoscaling.scala]: ../../../../test/scala/ohnosequences/awstools/autoscaling.scala.md
[test/scala/ohnosequences/awstools/instanceTypes.scala]: ../../../../test/scala/ohnosequences/awstools/instanceTypes.scala.md
[test/scala/ohnosequences/awstools/package.scala]: ../../../../test/scala/ohnosequences/awstools/package.scala.md
[test/scala/ohnosequences/awstools/sqs.scala]: ../../../../test/scala/ohnosequences/awstools/sqs.scala.md