package ohnosequences.saws.signing

import com.amazonaws.services.sqs.model.{SendMessageRequest, ListQueuesRequest}
import com.amazonaws.services.sqs.model.transform.{SendMessageRequestMarshaller, ListQueuesRequestMarshaller}
import java.net.{URI}

import ohnosequences.saws.signing.v4.{V4Signer}
import ohnosequences.saws.signing.v4.dispatch.{DispatchUtils, DispatchV4Data}

object DispatchTest {


  def main(args: Array[String]) {


    //SQS request
    val listQueuesRequest = new ListQueuesRequest()

    val sendMessageRequest =  new SendMessageRequest().withQueueUrl("http://sqs.eu-west-1.amazonaws.com/393321850454/gridTest").withMessageBody("amsterdam")

    //HTTP abstraction for it
    //val request = new ListQueuesRequestMarshaller().marshall(listQueuesRequest)
    val request = new SendMessageRequestMarshaller().marshall(sendMessageRequest)
    request.setEndpoint(new URI("http://sqs.eu-west-1.amazonaws.com"))


    val dispatchRequest = DispatchUtils.awsToDispatch(request)


    val credentials = Credentials.fromFile("AwsCredentials.properties")
    val additionalHeaders = V4Signer.sign(dispatchRequest.build(), credentials)(DispatchV4Data)
    val dispatchRequestSigned = DispatchUtils.applySigningResult(dispatchRequest, additionalHeaders)


    import dispatch._, Defaults._
    //sending request
    val result = Http(dispatchRequestSigned OK as.String)

    for (c <- result) {
      println(c)
    }

   // dreq.build()
    Thread.sleep(10000)

    println("---------------")

    // testing AWS v4 signer....
   // new FakeAWSV4().sign(request, new PropertiesCredentials("AwsCredentials.properties"))
  }

}
