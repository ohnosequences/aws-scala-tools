package ohnosequences.saws

import com.amazonaws.services.sqs.model.{SendMessageRequest, ListQueuesRequest}
import com.amazonaws.services.sqs.model.transform.{SendMessageRequestMarshaller, ListQueuesRequestMarshaller}
import java.net.URI

import ohnosequences.saws.signing.v2.{V2SigningProcess, V2Input}

import java.util.concurrent.CountDownLatch
import ohnosequences.saws.signing.{Signer, Credentials}
import ohnosequences.saws.signing.dispatchSign.{Dispatch2Implicits, DispatchImplicits, DispatchUtils}
import com.amazonaws.auth.{FakeAWSV2, PropertiesCredentials, FakeAWSV4}
import java.io.File
import com.ning.http.client.RequestBuilder
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest
import com.amazonaws.services.ec2.model.transform.DescribeSecurityGroupsRequestMarshaller

object DispatchV2Test {


  def main(args: Array[String]) {


    //SQS request
    //val listQueuesRequest = new ListQueuesRequest()

    val describeSecurity = new DescribeSecurityGroupsRequest()

    //HTTP abstraction for it
    // val request = new ListQueuesRequestMarshaller().marshall(listQueuesRequest)
    val request = new DescribeSecurityGroupsRequestMarshaller().marshall(describeSecurity)

    request.setEndpoint(new URI("http://ec2.eu-west-1.amazonaws.com"))


    val dispatchRequest = DispatchUtils.awsToDispatch(request)

    val credentials = Credentials.fromFile("AwsCredentials.properties")
    val signer = new Signer[V2SigningProcess.v.type, V2SigningProcess.type](V2SigningProcess)

    import Dispatch2Implicits._
    val dispatchRequestSigned = signer.sign(dispatchRequest.build(), credentials)


    new FakeAWSV2().sign(request, new PropertiesCredentials(new File("AwsCredentials.properties")))


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
