package ohnosequences

import ohnosequences.awstools.s3.S3
import java.io.File
import ohnosequences.awstools.ec2.EC2
import com.amazonaws.services.ec2.model.{DescribeReservedInstancesRequest, DescribeInstancesRequest}
import com.amazonaws.Request
import com.amazonaws.services.ec2.model.transform.{DescribeInstancesRequestMarshaller, DescribeReservedInstancesRequestMarshaller}
import java.net.URI
import java.util.Map.Entry
import com.amazonaws.auth.{QueryStringSigner, PropertiesCredentials}

import scala.collection.JavaConversions._

object Experiments {
  def main(args: Array[String]) {
//    val s3 = S3.create(new File("AwsCredentials.properties"))
//    s3.s3.doesBucketExist()

    val ec2 = EC2.create(new File("AwsCredentials.properties"))
    //val req =ec2.getInstanceById("id")
    val req = new DescribeInstancesRequest().withInstanceIds("i-84a964c9")
    val request: Request[DescribeInstancesRequest] = new DescribeInstancesRequestMarshaller().marshall(req)




    val endpoint = new URI("http://ec2.eu-west-1.amazonaws.com")
    request.setEndpoint(endpoint)

    val params: Map[String, String] = request.getOriginalRequest.copyPrivateRequestParameters().toMap

    for(entry <- params) {
      request.addParameter(entry._1, entry._2)
    }


    val credentials = new PropertiesCredentials(new File("AwsCredentials.properties"))
//    AWSCredentials credentials = awsCredentialsProvider.getCredentials();


    val originalRequest = request.getOriginalRequest()
//    if (originalRequest != null && originalRequest.getRequestCredentials() != null) {
//      credentials = originalRequest.getRequestCredentials();
//    }
//
//    ExecutionContext executionContext = createExecutionContext();
 //    executionContext.setSigner(signer);
//    executionContext.setCredentials(credentials);
//
//    StaxResponseHandler<X> responseHandler = new StaxResponseHandler<X>(unmarshaller);
//    DefaultErrorResponseHandler errorResponseHandler = new DefaultErrorResponseHandler(exceptionUnmarshallers);
//
//    return (X)client.execute(request, responseHandler, errorResponseHandler, executionContext);
   // println(req.)

    println(request.getHeaders)
    val signer = new QueryStringSigner()
    signer.sign(request, credentials)
    println(request.getParameters)
  }

}
