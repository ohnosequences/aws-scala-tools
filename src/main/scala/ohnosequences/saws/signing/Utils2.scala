package ohnosequences.saws.signing

import java.text.SimpleDateFormat
import java.util.Locale
import java.util
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import com.amazonaws.AmazonClientException
import java.lang.String
import scala.Predef.String
import org.apache.commons.codec.binary.Base64

object Utils2 {
  /** RFC 822 format */
  // val rfc822DateFormat: SimpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)

  def getCanonicalizedResourcePath(resourcePath: String): String = {
    if (resourcePath == null || resourcePath.length == 0) {
      "/"
    } else {
      var newPath = resourcePath
      if(newPath.startsWith("/")) {
        newPath= newPath.substring(1)
      }
      if(!newPath.endsWith("/")) {
        newPath = newPath + "/"
      }
      Utils.urlEncode(newPath, true)
    }
  }

  def getCanonicalizedQueryString(parameters: java.util.Map[String, String]): String = {
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

  def getBinaryRequestPayloadStreamWithoutQueryParams(content: InputStream): InputStream = {
    if (content == null) new ByteArrayInputStream(new Array[Byte](0)) else content
  }


  def getBinaryRequestPayloadWithoutQueryParams(content: InputStream): Array[Byte] = {
    val content2: InputStream = getBinaryRequestPayloadStreamWithoutQueryParams(content)
    try {
      content2.mark(-1)
      val byteArrayOutputStream: ByteArrayOutputStream = new ByteArrayOutputStream
      val buffer: Array[Byte] = new Array[Byte](1024 * 5)
      var stopped = false
      while (!stopped) {
        val bytesRead: Int = content2.read(buffer)
        if (bytesRead == -1) {
          stopped = true
        } else {
          byteArrayOutputStream.write(buffer, 0, bytesRead)
        }
      }
      byteArrayOutputStream.close
      content2.reset
      byteArrayOutputStream.toByteArray
    }
    catch {
      case e: Exception => {
        throw new Error("Unable to read request payload to sign request: " + e.getMessage, e)
      }
    }
  }


  def getRequestPayloadWithoutQueryParams(content: InputStream): String = {
    new String(getBinaryRequestPayloadWithoutQueryParams(content), "UTF-8")
  }

  def base64Encode(b: Array[Byte]): String = {
    new String(Base64.encodeBase64(b))
  }
}
