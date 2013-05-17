package ohnosequences.saws.signing.dispatchSign

import scala.collection.JavaConversions._
import com.ning.http.client.RequestBuilder
import java.net.URL

object DispatchUtils {
  def awsToDispatch(request: com.amazonaws.Request[_]): com.ning.http.client.RequestBuilder = {

    import dispatch._, Defaults._



    var file = request.getResourcePath
    if(!file.startsWith("/")) {
      file = "/" + file
    }
    val url2 = new URL(request.getEndpoint.toURL.getProtocol, request.getEndpoint.toURL.getHost,file)



    println(url2)
    var dreq = if (request.getHttpMethod.toString.equals("GET")) {
      new RequestBuilder(request.getHttpMethod.toString, true).setUrl(url2.toString).GET
    } else {
      (:/("www.test.com") / "test").POST
    }
    // vrequest.getContent
    val params: Map[String, String] = request.getParameters.toMap


    for ((p1, p2) <- params) {
      dreq = dreq.addParameter(p1, p2)
    }

    println(new URL(dreq.build().getUrl).getPath)


//    for ((p1, p2) <- request.getOriginalRequest.copyPrivateRequestParameters()) {
//      dreq = dreq.addParameter(p1, p2)
//    }

    val header: Map[String, String] = request.getHeaders.toMap
    for ((h1, h2) <- header) {
      dreq = dreq.addHeader(h1, h2)
    }
    dreq
  }

  def applySigningResult(requestBuilder: RequestBuilder, additionalHeaders: Map[String, String]): RequestBuilder = {
    // appliing additional headers
    var rb = requestBuilder
    for ((h1, h2) <- additionalHeaders) {
      rb = rb.addHeader(h1, h2)
    }
    rb
  }
}
