package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.{Utils, Signer, Credentials}
import scala.Predef._
import scala.collection.JavaConversions._
import java.util.{Date, SimpleTimeZone}
import java.io.{ByteArrayInputStream, InputStream}


import java.text.SimpleDateFormat
import scala.collection.parallel.mutable
import scala.collection


object V4Signer extends Signer {

  type Version = V4

  val AWS_HMAC = "AWS4-HMAC-SHA256"
  val TERMINATOR = "aws4_request"

  def sign[R](request: R, credentials: Credentials)(v4data: V4Data[R]): Map[String, String] = {

    val additionalHeaders = v4data.getAdditionalHeaders(request)
    val parameters = v4data.getParameters(request)
    val headers = v4data.getHeaders(request) ++ additionalHeaders
    val method = v4data.getMethod(request)
    val path = v4data.getResourcePath(request)
    val content = v4data.getContent(request)
    val usePayloadForQueryParameters = v4data.usePayloadForQueryParameters(method, content)
    val content256 = v4data.getContentSha256(request)
    val region = v4data.getRegionName(request)
    val service = v4data.getServiceName(request)

    val canonicalRequest = (new collection.mutable.StringBuilder()
      .append(method).append("\n")
      .append(Utils.getCanonicalizedResourcePath(path)).append("\n")
      .append(if (usePayloadForQueryParameters) "" else Utils.getCanonicalizedQueryString(parameters)).append("\n")
      .append(getCanonicalizedHeaderString(headers)).append("\n")
      .append(getSignedHeadersString(headers)).append("\n")
      .append(content256)
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
    val additionalParams = additionalHeaders ++ Map[String, String](
      "Authorization" -> authorizationHeader
    )
    additionalParams
  }

  def getCanonicalizedHeaderString(headers: Map[String, String]): String = {
    val buffer = new java.lang.StringBuilder
    for (header <- Utils.getSortedHeaders(headers)) {
      buffer.append(header.toLowerCase.replaceAll("\\s+", " ") + ":" + headers(header).replaceAll("\\s+", " "))
      buffer.append("\n")
    }
    buffer.toString
  }

  def getSignedHeadersString(headers: Map[String, String]): String = {
    val buffer: java.lang.StringBuilder = new java.lang.StringBuilder
    for (header <- Utils.getSortedHeaders(headers)) {
      if (buffer.length > 0) buffer.append(";")
      buffer.append(header.toLowerCase)
    }
    buffer.toString
  }
}