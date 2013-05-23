package ohnosequences.saws

import com.amazonaws.services.sqs.model.{SendMessageRequest, ListQueuesRequest}
import com.amazonaws.services.sqs.model.transform.{SendMessageRequestMarshaller, ListQueuesRequestMarshaller}
import java.net.URI

import ohnosequences.saws.signing.v4.{V4SigningProcess, V4Input}

import java.util.concurrent.CountDownLatch
import ohnosequences.saws.signing.{Signer, Credentials}
import ohnosequences.saws.signing.dispatchSign.{DispatchV4, DispatchUtils}
import com.amazonaws.auth.{PropertiesCredentials, FakeAWSV4}
import java.io.File
import com.ning.http.client.RequestBuilder

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
    val signer = new Signer[V4SigningProcess.v.type, V4SigningProcess.type](V4SigningProcess)

    import DispatchV4._
  //  val additionalHeaders = V4SigningProcess.apply(new DispatchV4(dispatchRequest.build()), credentials)
    val dispatchRequestSigned = signer.sign(dispatchRequest.build(), credentials)

    //val dispatchRequestSigned = DispatchUtils.applySigningResult(dispatchRequest, additionalHeaders)

    //new FakeAWSV4().sign(request, new PropertiesCredentials(new File("AwsCredentials.properties")))





    import dispatch._, Defaults._
    //sending request
    val result = Http(new RequestBuilder(dispatchRequestSigned) OK as.String)

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
