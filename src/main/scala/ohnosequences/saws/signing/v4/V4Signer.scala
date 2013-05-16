package ohnosequences.saws.signing.v4

import ohnosequences.saws.signing.{Signer, Utils, Credentials}
import java.lang.{String}
import scala.Predef._
import java.util
import scala.collection.JavaConversions._
import java.util.{Date, SimpleTimeZone, Collections}
import java.io.{UnsupportedEncodingException, ByteArrayInputStream, InputStream}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.NameValuePair

import java.text.SimpleDateFormat
import javax.crypto.spec.SecretKeySpec
import javax.crypto.Mac


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
    val CanonicalURI: String = getCanonicalizedResourcePath(path)
    val CanonicalQueryString: String = getCanonicalizedQueryString(method, content, parameters)
    val CanonicalHeaders = getCanonicalizedHeaderString(headers)
    val SignedHeaders = getSignedHeadersString(headers)
    val contentSha256 = v4data.getContentSha256(request)(v4data)

    val canonicalRequest = HTTPRequestMethod + "\n" + CanonicalURI + "\n" + CanonicalQueryString + "\n" + CanonicalHeaders + "\n" + SignedHeaders + "\n" + contentSha256

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


    val kSecret = ("AWS4" + credentials.secretKey).getBytes()
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

  def getCanonicalizedQueryString[R](method: String, content: InputStream, parameters: Map[String, String]): String = {
    if (usePayloadForQueryParameters(method, content)) ""
    else getCanonicalizedQueryString2(parameters)
  }

  def getCanonicalizedQueryString2(parameters: java.util.Map[String, String]): String = {
    val sorted = new util.TreeMap[String, String]
    sorted.putAll(parameters)
    val builder = new java.lang.StringBuilder()

    val pairs: java.util.Iterator[java.util.Map.Entry[String, String]] = sorted.entrySet.iterator
    while (pairs.hasNext) {
      val pair: java.util.Map.Entry[String, String] = pairs.next
      val key: String = pair.getKey
      val value: String = pair.getValue
      builder.append(Utils.urlEncode(key, false))
      builder.append("=")
      builder.append(Utils.urlEncode(value, false))
      if (pairs.hasNext) {
        builder.append("&")
      }
    }
    builder.toString
  }

  def getCanonicalizedResourcePath(resourcePath: String): String = {
    if (resourcePath == null || resourcePath.length == 0) {
      "/"
    }
    else {
      Utils.urlEncode(resourcePath, true)
    }
  }

  def getCanonicalizedHeaderString(headers: Map[String, String]): String = {
    val sortedHeaders: java.util.List[String] = new java.util.ArrayList[String]()
    sortedHeaders.addAll(headers.keySet)
    Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER)
    val buffer = new java.lang.StringBuilder

    for (header <- sortedHeaders) {
      buffer.append(header.toLowerCase.replaceAll("\\s+", " ") + ":" + headers(header).replaceAll("\\s+", " "))
      buffer.append("\n")
    }
    buffer.toString
  }

  def getSignedHeadersString(headers: Map[String, String]): String = {
    val sortedHeaders: java.util.List[String] = new java.util.ArrayList[String]()
    sortedHeaders.addAll(headers.keySet)
    Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER)
    val buffer: java.lang.StringBuilder = new java.lang.StringBuilder

    for (header <- sortedHeaders) {
      if (buffer.length > 0) buffer.append(";")
      buffer.append(header.toLowerCase)
    }
    buffer.toString
  }

  val DEFAULT_ENCODING: String = "UTF-8"

  def getBinaryRequestPayloadStream(method: String, content: InputStream, parameters: Map[String, String]): InputStream = {
    if (usePayloadForQueryParameters(method, content)) {
      val encodedParameters: String = encodeParameters(parameters)
      if (encodedParameters == null) return new ByteArrayInputStream(new Array[Byte](0))
      try {
        return new ByteArrayInputStream(encodedParameters.getBytes(DEFAULT_ENCODING))
      }
      catch {
        case e: UnsupportedEncodingException => {
          throw new Error("Unable to encode string into bytes")
        }
      }
    }
    getBinaryRequestPayloadStreamWithoutQueryParams(content)
  }


  def encodeParameters(parameters: Map[String, String]): String = {
    var nameValuePairs: java.util.List[NameValuePair] = null
    if (parameters.size > 0) {
      nameValuePairs = new util.ArrayList[NameValuePair](parameters.size)

      for (entry <- parameters.entrySet) {
        nameValuePairs.add(new BasicNameValuePair(entry.getKey, entry.getValue))
      }
    }
    var encodedParams: String = null



    if (nameValuePairs != null) {
      encodedParams = URLEncodedUtils.format(nameValuePairs, DEFAULT_ENCODING)
    }
    encodedParams
  }

  def getBinaryRequestPayloadStreamWithoutQueryParams(content: InputStream): InputStream = {
    if (!content.markSupported) {
      throw new Error("Unable to read request payload to sign request.")
    }
    if (content == null) new ByteArrayInputStream(new Array[Byte](0)) else content

  }

  def hmac(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    val alg: String = "HmacSHA256"
    val mac: Mac = Mac.getInstance(alg)
    mac.init(new SecretKeySpec(key, alg))
    return mac.doFinal(data)
  }
}