package ohnosequences.saws.signing.dispatchSign

import com.ning.http.client.{RequestBuilder, Request}
import java.net.URL
import ohnosequences.saws.signing.v4.V4Input

import scala.collection.JavaConversions._

class DispatchV4(request: Request) extends V4Input {

  def endpoint: String = request.getUrl

  def headers: Traversable[(String, String)] = new TraversableFromMap(request.getHeaders)

  def parameters: Traversable[(String, String)] = new TraversableFromMap(request.getParams)

  def content: Array[Byte] = {
    val bytes = request.getByteData
    if(bytes == null) {
      new Array[Byte](0)
    } else {
      bytes
    }
  }

  def method: String = request.getMethod

  def resource: String = {
    val url = new URL(request.getUrl)
    url.getPath
  }

  def region: String = "eu-west-1"

  def service: String = "sqs"

  class TraversableFromMap(map: java.util.Map[String, java.util.List[String]]) extends Traversable[(String, String)] {
    def foreach[U](f: ((String, String)) => U) {
      if(map != null) {
        map.foreach{ case (key, values) =>
          values.foreach(f(key, _))
        }
      }
    }
  }
}

object DispatchV4 {
  implicit def dispatchRequestToV4Input(request: Request): V4Input = new DispatchV4(request)
  implicit def applyOutput(request: Request, output: Map[String, String]): Request = {
    val builder = new RequestBuilder(request)
    output.foreach { case (key, value) =>
      builder.setHeader(key, value)
    }
    builder.build()
  }
}

