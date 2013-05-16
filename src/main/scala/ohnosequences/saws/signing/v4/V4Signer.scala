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

    //create canonical
    val HTTPRequestMethod: String = v4data.getMethod(request)
    val CanonicalURI: String = getCanonicalizedResourcePath(v4data.getResourcePath(request))
    val CanonicalQueryString: String = getCanonicalizedQueryString(request)(v4data)
    val CanonicalHeaders = getCanonicalizedHeaderString(request)(v4data)
    val SignedHeaders = getSignedHeadersString(request)(v4data)
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
    val signedHeadersAuthorizationHeader: String = "SignedHeaders=" + getSignedHeadersString(request)(v4data)
    val signatureAuthorizationHeader: String = "Signature=" + Utils.toHex(signature)
    val authorizationHeader: String = AWS_HMAC + " " + credentialsAuthorizationHeader + ", " + signedHeadersAuthorizationHeader + ", " + signatureAuthorizationHeader

   // Predef.println("authorizationHeader: " + authorizationHeader)
    val additionalParams = v4data.getAdditionalHeaders(request) ++ Map[String, String](
      "Authorization" -> authorizationHeader
    )
    additionalParams
  }


  def usePayloadForQueryParameters[R](request: R)(v4data: V4Data[R]): Boolean = {
    val requestIsPOST: Boolean = "POST".equals(v4data.getMethod(request))
    val requestHasNoPayload: Boolean = (v4data.getContent(request) == null)
    requestIsPOST && requestHasNoPayload
  }

  def getCanonicalizedQueryString[R](request: R)(v4data: V4Data[R]): String = {
    if (usePayloadForQueryParameters(request)(v4data)) return ""
    else getCanonicalizedQueryString2(v4data.getParameters(request))
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

  def getCanonicalizedHeaderString[R](request: R)(v4data: V4Data[R]): String = {
    val sortedHeaders: java.util.List[String] = new java.util.ArrayList[String]()
    sortedHeaders.addAll(v4data.getHeaders(request).keySet)
    Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER)
    val buffer = new java.lang.StringBuilder

    for (header <- sortedHeaders) {
      buffer.append(header.toLowerCase.replaceAll("\\s+", " ") + ":" + v4data.getHeaders(request)(header).replaceAll("\\s+", " "))
      buffer.append("\n")
    }
    buffer.toString
  }

  def getSignedHeadersString[R](request: R)(v4data: V4Data[R]): String = {
    val sortedHeaders: java.util.List[String] = new java.util.ArrayList[String]()
    sortedHeaders.addAll(v4data.getHeaders(request).keySet)
    Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER)
    val buffer: java.lang.StringBuilder = new java.lang.StringBuilder

    for (header <- sortedHeaders) {
      if (buffer.length > 0) buffer.append(";")
      buffer.append(header.toLowerCase)
    }
    buffer.toString
  }

  val DEFAULT_ENCODING: String = "UTF-8"

  def getBinaryRequestPayloadStream[R](request: R)(v4data: V4Data[R]): InputStream = {
    if (usePayloadForQueryParameters(request)(v4data)) {
      val encodedParameters: String = encodeParameters(request)(v4data)
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
    getBinaryRequestPayloadStreamWithoutQueryParams(v4data.getContent(request))
  }


  def encodeParameters[R](request: R)(v4data: V4Data[R]): String = {
    var nameValuePairs: java.util.List[NameValuePair] = null
    if (v4data.getParameters(request).size > 0) {
      nameValuePairs = new util.ArrayList[NameValuePair](v4data.getParameters(request).size)
      import scala.collection.JavaConversions._
      for (entry <- v4data.getParameters(request).entrySet) {
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