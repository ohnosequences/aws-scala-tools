package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.{Utils, Credentials}
import scala.Predef._

import java.util.{Date, SimpleTimeZone}
import java.text.SimpleDateFormat
import ohnosequences.saws.signing.SigningProcess
import java.net.URL
import scala.collection.immutable.TreeMap


object V4SigningProcess extends SigningProcess(v4) {

  val AWS_HMAC = "AWS4-HMAC-SHA256"
  val TERMINATOR = "aws4_request"

  def calculateAdditionalHeaders(endpoint: String, contentSha256: String): Map[String, String] = {
    val dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))

    val date: Date = new Date()

    val dateTime: String = dateTimeFormat.format(date)

    //fix host!!!
    val hostHeader: String = new URL(endpoint).getHost

    Map[String, String](
    "Host" -> hostHeader,
    "X-Amz-Date" -> dateTime,
    "x-amz-content-sha256" -> contentSha256
    )
  }

  def usePayload(method: String, content: Array[Byte]) = "POST".equals(method) && (content.length == 0)

  def getPayload(method: String, content: Array[Byte], parameters: Traversable[(String, String)]): Array[Byte] = {

    if (usePayload(method, content)) {
      Utils.encodeParameters(parameters) match {
        case Some(encodedParameters) =>  encodedParameters.getBytes(Utils.UTF8_ENCODING)
        case None => new Array[Byte](0)
      }
    } else {
      content
    }
  }

  def getContentSha256(method: String, content: Array[Byte], parameters: Traversable[(String, String)]): String =  {
    val payloadStream = getPayload(method, content, parameters)
    Utils.toHex(Utils.hash(payloadStream))
  }

  def apply(input: v4.Input, credentials: Credentials): v4.Output = {

    val method = input.method
    val content = input.content
    val parameters = input.parameters
    val endpoint = input.endpoint
    val service = input.service
    val region = input.region
    val resource = input.resource

    val contentSha256 = getContentSha256(method, content, parameters)
    val additionalHeaders = calculateAdditionalHeaders(endpoint,contentSha256)


    //sort...
    val headers = Utils.sort(input.headers) ++ additionalHeaders

    val canonicalRequest = (new StringBuilder()
      .append(method).append("\n")
      .append(Utils.getCanonicalizedResourcePath(resource)).append("\n")
      .append(if (usePayload(method, content)) "" else Utils.getCanonicalizedQueryString(parameters)).append("\n")
      .append(getCanonicalizedHeaderString(headers)).append("\n")
      .append(getSignedHeadersString(headers)).append("\n")
      .append(getContentSha256(method, content, parameters))
    ).toString()

//    Predef.println("canonical request:")
//    Predef.println(canonicalRequest)

    val date: Date = new Date
    val dateStampFormat = new SimpleDateFormat("yyyyMMdd")
    dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))
    val dateStamp: String = dateStampFormat.format(date)
    val dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))
    val dateTime: String = dateTimeFormat.format(date)
    val scope: String = dateStamp + "/" + region + "/" + service + "/" + TERMINATOR
    val signingCredentials: String = credentials.accessKey + "/" + scope


    val stringToSign: String = AWS_HMAC + "\n" + dateTime + "\n" + scope + "\n" + Utils.toHex(Utils.hash(canonicalRequest))
//    Predef.println("string to sign:")
//    Predef.println(stringToSign)

    val kSecret = "AWS4" + credentials.secretKey
    val kDate = Utils.hmac(dateStamp, kSecret)
    val kRegion = Utils.hmac(region, kDate)
    val kService = Utils.hmac(service, kRegion)
    val kSigning = Utils.hmac(TERMINATOR, kService)
    val signature = Utils.hmac(stringToSign, kSigning)

    val credentialsAuthorizationHeader = "Credential=" + signingCredentials
    val signedHeadersAuthorizationHeader = "SignedHeaders=" + getSignedHeadersString(headers)
    val signatureAuthorizationHeader = "Signature=" + Utils.toHex(signature)
    val authorizationHeader = AWS_HMAC + " " +
      credentialsAuthorizationHeader + ", " +
      signedHeadersAuthorizationHeader + ", " +
      signatureAuthorizationHeader

   // Predef.println("authorizationHeader: " + authorizationHeader)
    val result = additionalHeaders ++ Map[String, String](
      "Authorization" -> authorizationHeader
    )
    result
  }

  def getCanonicalizedHeaderString(sortedHeaders: TreeMap[String, String]): String = {
    val builder = new StringBuilder()
    sortedHeaders.foreach { case (key, value) =>
      builder.append(key.toLowerCase.replaceAll("\\s+", " ") + ":" + value.replaceAll("\\s+", " "))
      builder.append("\n")
    }
    builder.toString()
  }

  def getSignedHeadersString(sortedHeaders: TreeMap[String, String]): String = {
    val builder = new StringBuilder()
    sortedHeaders.foreach { case (key, value) =>
      if (builder.length > 0) builder.append(";")
      builder.append(key.toLowerCase)
    }
    builder.toString()
  }
}