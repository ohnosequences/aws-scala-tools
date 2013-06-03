package ohnosequences.saws.signing.v3

import ohnosequences.saws.signing.{Utils, Credentials, SigningProcess}
import java.text.SimpleDateFormat
import java.util.{Locale, Date, SimpleTimeZone}
import java.net.URL
import scala.collection.immutable.TreeMap


object V3SigningProcess extends SigningProcess(v3) {
  def apply(input: V3Input, credentials: Credentials): Map[String, String] = {
    val method = input.method
    val content = input.content
    val parameters = input.parameters
    val endpoint = input.endpoint
    val service = input.service
    val region = input.region
    val resource = input.resource

    val additionalHeaders = calculateAdditionalHeaders(endpoint)
    val sortedParams = Utils.sortCaseIns(parameters)
    val headers = Utils.sortCaseIns(input.headers) ++ additionalHeaders

    val canonicalRequest = (new StringBuilder()
      .append(method).append("\n")
      .append(Utils.getCanonicalizedResourcePath(resource)).append("\n")
      .append(Utils.getCanonicalizedQueryString(sortedParams)).append("\n")
      .append(getCanonicalizedHeaderString(headers)).append("\n")
      .append(Utils.getRequestPayloadWithoutQueryParams(content))
    ).toString()

    //println("V3 : -------------\n" + canonicalRequest + "\n---")
    val bytesToSign = Utils.hash(canonicalRequest)
    //println("bytes V3: " + Utils.toHex(bytesToSign))

    val signature = Utils.base64Encode(Utils.hmac(canonicalRequest, credentials.secretKey))

    //todo isHttps ? HTTPS_SCHEME : HTTP_SCHEME

    val signatureHeader = (new StringBuilder()
      .append("AWS3").append(" ")
      .append("AWSAccessKeyId=" + credentials.accessKey + ",")
      .append("Algorithm=" + Utils.HMAC_SHA_256 + ",")
      .append("Signature=", signature)
    ).toString()

    Map[String, String](
      "X-Amzn-Authorization" -> signatureHeader
    )
  }

  def getCanonicalizedHeaderString(sortedHeaders: TreeMap[String, String]): String = {
    val builder = new StringBuilder()
    sortedHeaders.foreach {
      case (key, value) if (key.toLowerCase.startsWith("x-amz") || key.toLowerCase.startsWith("host")) =>
        builder.append(key.toLowerCase + ":" + value)
        builder.append("\n")
      case _ => ()
    }
    builder.toString()
  }

  def calculateAdditionalHeaders(endpoint: String): Map[String, String] = {
    val rfc822DateFormat: SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
    rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"))

    val date: Date = new Date()

    val dateTime: String = rfc822DateFormat.format(date)

    //fix host!!!
    val hostHeader: String = new URL(endpoint).getHost
    /** RFC 822 format */

    //todo not standart port
    Map[String, String](
      "Date" -> dateTime,
      "X-Amz-Date" -> dateTime,
      "Host" -> hostHeader
    )
  }
}