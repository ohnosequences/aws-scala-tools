package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.{Utils, Signer, Credentials}
import scala.Predef._
import scala.collection.JavaConversions._
import java.util.{Date, SimpleTimeZone}
import java.io.{ByteArrayInputStream, InputStream}


import java.text.SimpleDateFormat



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

    //create canonical
    val HTTPRequestMethod: String = v4data.getMethod(request)
    val CanonicalURI: String = Utils.getCanonicalizedResourcePath(path)
    val CanonicalQueryString: String = getCanonicalizedQueryString(method, content, parameters)
    val CanonicalHeaders = getCanonicalizedHeaderString(headers)
    val SignedHeaders = getSignedHeadersString(headers)
    val ContentSha256 = v4data.getContentSha256(request)(v4data)

    val canonicalRequest = HTTPRequestMethod + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders + "\n" + SignedHeaders + "\n" + ContentSha256

//    Predef.println("canonical request:")
//    Predef.println(canonicalRequest)


    val dateStampFormat = new SimpleDateFormat("yyyyMMdd")
    dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))


    var date: Date = new Date
    val dateStamp: String = dateStampFormat.format(date)

    val dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"))
    val dateTime: String = dateTimeFormat.format(date)
    val scope: String = dateStamp + "/" + v4data.getRegionName(request) + "/" + v4data.getServiceName(request) + "/" + TERMINATOR


    val signingCredentials: String = credentials.accessKey + "/" + scope


    val stringToSign: String = AWS_HMAC + "\n" + dateTime + "\n" + scope + "\n" + Utils.toHex(Utils.hash(canonicalRequest))
//    Predef.println("string to sign:")
//    Predef.println(stringToSign)


    val kSecret = "AWS4" + credentials.secretKey
    val kDate = Utils.hmac(dateStamp, kSecret)
    val kRegion = Utils.hmac(v4data.getRegionName(request), kDate)
    val kService = Utils.hmac(v4data.getServiceName(request), kRegion)
    val kSigning = Utils.hmac(TERMINATOR, kService)
    val signature = Utils.hmac(stringToSign, kSigning)


    val credentialsAuthorizationHeader: String = "Credential=" + signingCredentials
    val signedHeadersAuthorizationHeader: String = "SignedHeaders=" + getSignedHeadersString(headers)
    val signatureAuthorizationHeader: String = "Signature=" + Utils.toHex(signature)
    val authorizationHeader: String = AWS_HMAC + " " + credentialsAuthorizationHeader + ", " + signedHeadersAuthorizationHeader + ", " + signatureAuthorizationHeader

   // Predef.println("authorizationHeader: " + authorizationHeader)
    val additionalParams = additionalHeaders ++ Map[String, String](
      "Authorization" -> authorizationHeader
    )
    additionalParams
  }

  def usePayloadForQueryParameters(method: String, content: InputStream): Boolean = {
    val requestIsPOST: Boolean = "POST".equals(method)
    val requestHasNoPayload: Boolean = (content == null)
    requestIsPOST && requestHasNoPayload
  }

  def getCanonicalizedQueryString(method: String, content: InputStream, parameters: Map[String, String]): String = {
    if (usePayloadForQueryParameters(method, content)) ""
    else Utils.getCanonicalizedQueryString(parameters)
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


  def getBinaryRequestPayloadStream(method: String, content: InputStream, parameters: Map[String, String]): InputStream = {
    if (usePayloadForQueryParameters(method, content)) {
      Utils.encodeParameters(parameters) match {
        case Some(encodedParameters) =>  new ByteArrayInputStream(encodedParameters.getBytes(Utils.UTF8_ENCODING))
        case None => new ByteArrayInputStream(new Array[Byte](0))
      }
    } else {
      Utils.getBinaryRequestPayloadStreamWithoutQueryParams(content)
    }
  }
}