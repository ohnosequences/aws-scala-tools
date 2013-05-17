package ohnosequences.saws.signing.dispatchSign

import scala.collection.JavaConversions._
import com.ning.http.client.RequestBuilder
import java.net.URL

object DispatchUtils {
  def awsToDispatch(request: com.amazonaws.Request[_]): com.ning.http.client.RequestBuilder = {

    import dispatch._

    var file = if(request.getResourcePath == null) "/" else request.getResourcePath

    if(!file.startsWith("/")) {
      file = "/" + file
    }

    val url = new URL(request.getEndpoint.toURL.getProtocol, request.getEndpoint.toURL.getHost, file)
    val fullURL = url.toString

    //println(fullURL)

    var dispatchRequest = new RequestBuilder(request.getHttpMethod.toString, true).setUrl(fullURL.toString)

    if (request.getHttpMethod.toString.equals("GET")) {
      dispatchRequest = dispatchRequest.GET
    } else {
      dispatchRequest = dispatchRequest.POST
    }
    // todo content!!!!

    val params: Map[String, String] = request.getParameters.toMap
    for ((p1, p2) <- params) {
      dispatchRequest = dispatchRequest.addParameter(p1, p2)
    }

    val header: Map[String, String] = request.getHeaders.toMap
    for ((h1, h2) <- header) {
      dispatchRequest = dispatchRequest.addHeader(h1, h2)
    }
    dispatchRequest
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
