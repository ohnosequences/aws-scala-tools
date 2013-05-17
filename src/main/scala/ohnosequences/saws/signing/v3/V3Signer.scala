package ohnosequences.saws.signing.v3

import ohnosequences.saws.signing.{Utils, Credentials, Signer}

import scala.Predef.String
import java.util._
import scala.collection.JavaConversions._
import scala.Predef.Map
import scala.List
import java.lang.{StringBuilder, String}
import scala.Predef.String
import scala.StringBuilder
import ohnosequences.saws.DispatchV3Test


object V3Signer extends Signer {
  type Version = V3

  def sign[R](request: R, credentials: Credentials)(v3data: V3Data[R]): Map[String, String] = {
    val nonce: String = UUID.randomUUID.toString

    val additionalHeaders = v3data.getAdditionalHeaders(request)
    val parameters = v3data.getParameters(request)
    val headers = v3data.getHeaders(request) ++ additionalHeaders
    val method = v3data.getMethod(request)
    val path = v3data.getResourcePath(request)
    val content = v3data.getContent(request)


    val stringToSign = method + "\n" +
      Utils.getCanonicalizedResourcePath(path) + "\n" +
      Utils.getCanonicalizedQueryString(parameters) + "\n" +
      getCanonicalizedHeadersForStringToSign(headers) + "\n" +
      Utils.getRequestPayloadWithoutQueryParams(content)

     val bytesToSign: Array[Byte] = Utils.hash(stringToSign)

    Predef.println("V3: Calculated StringToSign: ----------\n" + stringToSign + "\n----------")
    Predef.println(stringToSign.length)
    DispatchV3Test.setS3(stringToSign)

    val isHttps = false




    val AUTHORIZATION_HEADER: String = "X-Amzn-Authorization"
    val NONCE_HEADER: String = "x-amz-nonce"
    val HTTP_SCHEME: String = "AWS3"
    val HTTPS_SCHEME: String = "AWS3-HTTPS"
    val algorithm = "HmacSHA256"


    val signatureRaw = Utils.hmac(bytesToSign, credentials.accessKey)
    val signature: String = Utils.base64Encode(signatureRaw)

    val builder = new java.lang.StringBuilder()
    builder.append(if (isHttps) HTTPS_SCHEME else HTTP_SCHEME).append(" ")
    builder.append("AWSAccessKeyId=" + credentials.accessKey + ",")
    builder.append("Algorithm=" + algorithm + ",")

    if (!isHttps) {
      builder.append(getSignedHeadersComponent(headers) + ",")
    }

    builder.append("Signature=" + signature)

    Predef.println("v3  : " + builder.toString)

   // request.addHeader(AUTHORIZATION_HEADER, builder.toString)


    //!!
    //    request.addHeader("Date", date)
    //    request.addHeader("X-Amz-Date", date)
    Map[String, String]()

  }

  def getCanonicalizedHeadersForStringToSign(headers: Map[String, String]): String = {
    val headersToSign: List[String] = getHeadersForStringToSign(headers).map(_.toLowerCase)

    val sortedHeaderMap: SortedMap[String, String] = new TreeMap[String, String]

    for (entry <- headers.entrySet) {
      if (headersToSign.contains(entry.getKey.toLowerCase)) {
        sortedHeaderMap.put(entry.getKey.toLowerCase, entry.getValue)
      }
    }
    val builder = new java.lang.StringBuilder

    for (entry <- sortedHeaderMap.entrySet) {
      builder.append(entry.getKey.toLowerCase).append(":").append(entry.getValue).append("\n")
    }
    builder.toString
  }

  def getHeadersForStringToSign(headers: Map[String, String]): List[String] = {
    val headersToSign = new java.util.ArrayList[String]()

    for (entry <- headers.entrySet) {
      val key: String = entry.getKey
      val lowerCaseKey: String = key.toLowerCase
      if (lowerCaseKey.startsWith("x-amz") || (lowerCaseKey == "host")) {
        headersToSign.add(key)
      }
    }
    Collections.sort(headersToSign)
    headersToSign.toList
  }

  private def getSignedHeadersComponent(headers: Map[String, String]): String = {
    val builder = new java.lang.StringBuilder
    builder.append("SignedHeaders=")
    var first: Boolean = true

    for (header <- getHeadersForStringToSign(headers)) {
      if (!first) builder.append(";")
      builder.append(header)
      first = false
    }
    builder.toString
  }
}