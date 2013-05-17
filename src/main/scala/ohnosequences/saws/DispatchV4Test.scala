package ohnosequences.saws

import com.amazonaws.services.sqs.model.{SendMessageRequest, ListQueuesRequest}
import com.amazonaws.services.sqs.model.transform.{SendMessageRequestMarshaller, ListQueuesRequestMarshaller}
import java.net.URI

import ohnosequences.saws.signing.v4.V4Signer

import java.util.concurrent.CountDownLatch
import ohnosequences.saws.signing.Credentials
import ohnosequences.saws.signing.dispatchSign.{DispatchV4Data, DispatchUtils}
import com.amazonaws.auth.{PropertiesCredentials, FakeAWSV4}
import java.io.File

object DispatchV4Test {


  def main(args: Array[String]) {


    //SQS request
    val listQueuesRequest = new ListQueuesRequest()

    val message = System.currentTimeMillis() + "test"
    val sendMessageRequest =  new SendMessageRequest().withQueueUrl("http://sqs.eu-west-1.amazonaws.com/393321850454/gridTest").withMessageBody(message)

    //HTTP abstraction for it
   // val request = new ListQueuesRequestMarshaller().marshall(listQueuesRequest)
    val request = new SendMessageRequestMarshaller().marshall(sendMessageRequest)
    request.setEndpoint(new URI("http://sqs.eu-west-1.amazonaws.com"))


    val dispatchRequest = DispatchUtils.awsToDispatch(request)


    val credentials = Credentials.fromFile("AwsCredentials.properties")
    val additionalHeaders = V4Signer.sign(dispatchRequest.build(), credentials)(DispatchV4Data)
    val dispatchRequestSigned = DispatchUtils.applySigningResult(dispatchRequest, additionalHeaders)

    new FakeAWSV4().sign(request, new PropertiesCredentials(new File("AwsCredentials.properties")))

    import dispatch._, Defaults._
    //sending request
    val result = Http(dispatchRequestSigned OK as.String)

    val latch = new CountDownLatch(1)
    for (c <- result) {
      println(c)
      latch.countDown()

    }

   // dreq.build()
    latch.await()

    println("---------------")

  //  testing AWS v4 signer....

  }

}
