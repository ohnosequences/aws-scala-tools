package ohnosequences.saws.signing.v4.dispatch

import scala.collection.JavaConversions._
import com.ning.http.client.RequestBuilder

object DispatchUtils {
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

  def applySigningResult(requestBuilder: RequestBuilder, additionalHeaders: Map[String, String]): RequestBuilder = {
    // appliing additional headers
    var rb = requestBuilder
    for ((h1, h2) <- additionalHeaders) {
      rb = rb.addHeader(h1, h2)
    }
    rb
  }
}
