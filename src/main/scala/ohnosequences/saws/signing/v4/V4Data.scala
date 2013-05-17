package ohnosequences.saws.signing.v4

import java.io.{IOException, InputStream}
import com.ning.http.client.{Request}

import scala.collection.JavaConversions._

import com.amazonaws.util.{BinaryUtils, HttpUtils}
import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date, SimpleTimeZone}
import com.amazonaws.AmazonClientException
import Predef._
import ohnosequences.saws.signing.Utils

trait V4Data[R] {
  def getEndPoint(r: R): String
  def getHeaders(r: R): Map[String, String]
  def getParameters(r: R): Map[String, String]
  def getMethod(r: R): String
  def getContent(r: R): InputStream
  def getResourcePath(r: R): String

  def getContentSha256(request: R)(v4data: V4Data[R]): String =  {

    val payloadStream: InputStream = V4Signer.getBinaryRequestPayloadStream(
      getMethod(request),
      getContent(request),
      getParameters(request)
    )
    payloadStream.mark(-1)
    val contentSha256: String = Utils.toHex(Utils.hash(payloadStream))

    try {
      payloadStream.reset
    }
    catch {
      case e: IOException => {
        throw new AmazonClientException("Unable to reset stream after calculating AWS4 signature", e)
      }
    }

    contentSha256
  }


  def getRegionName(r: R): String

  def getServiceName(r: R): String

  //some errors here!
  def getAdditionalHeaders(r: R): Map[String, String] = {
    val dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))

    var date: Date = new Date

    val dateTime: String = dateTimeFormat.format(date)

    //fix host!!!
    val hostHeader: String = new URL(getEndPoint(r)).getHost

    Map[String, String](
      "Host" -> hostHeader,
      "X-Amz-Date" -> dateTime,
      "x-amz-content-sha256" -> getContentSha256(r)(this)
    )
  }


}




