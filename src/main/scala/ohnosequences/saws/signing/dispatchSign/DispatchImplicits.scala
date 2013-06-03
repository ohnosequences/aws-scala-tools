package ohnosequences.saws.signing.dispatchSign

import com.ning.http.client.{RequestBuilder, Request}
import java.net.URL
import ohnosequences.saws.signing.v4.V4Input

import scala.collection.JavaConversions._
import ohnosequences.saws.signing.v2.V2Input
import ohnosequences.saws.signing.v3.V3Input


class DispatchV3(request: Request) extends V3Input {

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

  def service: String = "s3"

  //rewrite it for better performance
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

  //rewrite it for better performance
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

class DispatchV2(request: Request) extends V2Input {

  def endpoint: String = {
    new URL(request.getUrl).getHost
  }

  def parameters: Traversable[(String, String)] = new TraversableFromMap(request.getParams)

  def resource: String = {
    val url = new URL(request.getUrl)
    url.getPath
  }

  //rewrite it for better performance
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

object DispatchImplicits {
  implicit def dispatchRequestToV4Input(request: Request): V4Input = new DispatchV4(request)
  implicit def applyOutput(request: Request, output: Map[String, String]): Request = {
    val builder = new RequestBuilder(request)
    output.foreach { case (key, value) =>
      builder.setHeader(key, value)
    }
    builder.build()
  }
}

object Dispatch2Implicits {
  implicit def dispatchRequestToV2Input(request: Request): V2Input = new DispatchV2(request)
  implicit def applyOutput(request: Request, output: Map[String, String]): Request = {
    val builder = new RequestBuilder(request)
    output.foreach { case (key, value) =>
      builder.addParameter(key, value)
    }
    builder.build()
  }
}

object Dispatch3Implicits {
  implicit def dispatchRequestToV3Input(request: Request): V3Input = new DispatchV3(request)
  implicit def applyOutput(request: Request, output: Map[String, String]): Request = {
    val builder = new RequestBuilder(request)
    output.foreach { case (key, value) =>
      builder.addParameter(key, value)
    }
    builder.build()
  }
}

