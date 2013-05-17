package ohnosequences.saws.signing.dispatchSign

import com.ning.http.client.Request
import java.io.InputStream
import java.net.URL


import scala.collection.JavaConversions._
import ohnosequences.saws.signing.v3.V3Data

object DispatchV3Data extends V3Data[Request] {

  def headMap(map: java.util.Map[String, java.util.List[String]]): Map[String, String] = {
    map.mapValues(_.head).toMap
  }

  def getEndpoint(r: Request): String = r.getUrl

  def getHeaders(r: Request): Map[String, String] = {
    val headers = if(r.getHeaders == null) Map[String, String]() else headMap(r.getHeaders)
    headers ++ getAdditionalHeaders(r)
  }

  def getParameters(r: Request): Map[String, String] = {
    if(r.getParams == null) Map[String, String]() else headMap(r.getParams)

  }

  def getMethod(r: Request): String = r.getMethod

  def getContent(r: Request): InputStream = {
    r.getStreamData
  }

  def getResourcePath(r: Request): String = {
    r.getUrl
    val url = new URL(r.getUrl)

    url.getPath
  }

  def getRegionName(r: Request) = {
    "eu-west-1"
  }

  def getServiceName(r: Request) = {
    "s3"
  }
}