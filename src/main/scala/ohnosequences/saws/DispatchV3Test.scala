package ohnosequences.saws


import ohnosequences.saws.signing.v3.V3Signer

import java.util.concurrent.CountDownLatch
import ohnosequences.saws.signing.Credentials
import ohnosequences.saws.signing.dispatchSign.{DispatchV3Data, DispatchV4Data, DispatchUtils}
import com.amazonaws.services.s3.model.{ListObjectsRequest, ListBucketsRequest}
import com.amazonaws.services.s3.{FakeS3Client, AmazonS3Client}
import com.amazonaws.auth.{FakeAWSV3, PropertiesCredentials}
import java.io.File
import com.amazonaws.http.HttpMethodName
import com.amazonaws.services.s3.model.transform.Unmarshallers

object DispatchV3Test {
  var fake = ""
  var s3 = ""

  def setS3(s: String) {
    s3 = s
  }

  def setFake(s: String) {
    fake = s
  }

  def main(args: Array[String]) {


    //SQS request
    val awsCredentials = new PropertiesCredentials(new File("AwsCredentials.properties"))

    val s3client = new FakeS3Client(awsCredentials)
    s3client.setEndpoint("http://s3-eu-west-1.amazonaws.com")

    //println(s3client.listBuckets())

//    val request= s3client.createRequest(null, null, new ListBucketsRequest(), HttpMethodName.GET)
//    val result = s3client.invoke(request, new Unmarshallers.ListBucketsUnmarshaller(), null, null)

   // val request = s3client.listObjects("testGridBucket")

    val listObjectsRequest = new ListObjectsRequest("testGridBucket", null, null, null, null)

      val request = s3client.createRequest(listObjectsRequest.getBucketName, null, listObjectsRequest, HttpMethodName.GET)
      if (listObjectsRequest.getPrefix != null) request.addParameter("prefix", listObjectsRequest.getPrefix)
      if (listObjectsRequest.getMarker != null) request.addParameter("marker", listObjectsRequest.getMarker)
      if (listObjectsRequest.getDelimiter != null) request.addParameter("delimiter", listObjectsRequest.getDelimiter)
      if (listObjectsRequest.getMaxKeys != null && listObjectsRequest.getMaxKeys.intValue >= 0) request.addParameter("max-keys", listObjectsRequest.getMaxKeys.toString)
  //  s3client.invoke(request, new Unmarshallers.ListObjectsUnmarshaller, listObjectsRequest.getBucketName, null)



    val dispatchRequest = DispatchUtils.awsToDispatch(request)



    val credentials = Credentials.fromFile("AwsCredentials.properties")
    val additionalHeaders = V3Signer.sign(dispatchRequest.build(), credentials)(DispatchV3Data)

    new FakeAWSV3().sign(request, awsCredentials)

    ///checks
    for(i <- 0 to fake.length) {
      if()
    }

    //val dispatchRequestSigned = DispatchUtils.applySigningResult(dispatchRequest, additionalHeaders)


//    import dispatch._
//    //sending request
//    val result = Http(dispatchRequestSigned OK as.String)
//
//    val latch = new CountDownLatch(1)
//    for (c <- result) {
//      println(c)
//      latch.countDown()
//
//    }
//
//    // dreq.build()
//    latch.await()
//
//    println("---------------")

    // testing AWS v4 signer....
    // new FakeAWSV4().sign(request, new PropertiesCredentials("AwsCredentials.properties"))
  }

}
