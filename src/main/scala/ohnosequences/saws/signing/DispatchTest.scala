package ohnosequences.saws.signing

import com.amazonaws.services.sqs.model.ListQueuesRequest
import com.amazonaws.services.sqs.model.transform.ListQueuesRequestMarshaller
import java.net.{URI}
import scala.collection.JavaConversions._
import ohnosequences.saws.signing.v4.{V4Signer}
import ohnosequences.saws.signing.v4.dispatch.DispatchV4Data

object DispatchTest {
  def awsToDispatch(request: com.amazonaws.Request[_]): com.ning.http.client.RequestBuilder = {
    import dispatch._, Defaults._
    var dreq = url("http://sqs.eu-west-1.amazonaws.com").POST
    // vrequest.getContent
    val params: Map[String, String] = request.getParameters.toMap


    for ((p1, p2) <- params) {
      dreq = dreq.addParameter(p1, p2)
    }

    val header: Map[String, String] = request.getHeaders.toMap
    for ((h1, h2) <- header) {
      dreq = dreq.addHeader(h1, h2)
    }
    dreq
  }

  def main(args: Array[String]) {


    //SQS request
    val listQueuesRequest = new ListQueuesRequest()

    //HTTP abstraction for it
    val request = new ListQueuesRequestMarshaller().marshall(listQueuesRequest)
    request.setEndpoint(new URI("http://sqs.eu-west-1.amazonaws.com"))


    var dispatchRequest = awsToDispatch(request)



    val credentials = Credentials.fromFile("AwsCredentials.properties")
    val additionalHeaders = V4Signer.sign(dispatchRequest.build(), credentials)(DispatchV4Data)


    // appliing additional headers
    for ((h1, h2) <- additionalHeaders) {
      dispatchRequest = dispatchRequest.addHeader(h1, h2)
    }


    import dispatch._, Defaults._
    //sending request
    val result = Http(dispatchRequest OK as.String)
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
