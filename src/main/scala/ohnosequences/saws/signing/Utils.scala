package ohnosequences.saws.signing

import java.util.{Collections, Locale}
import java.util
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, InputStream}
import org.apache.commons.codec.binary.Base64
import java.net.URLEncoder
import java.security.{DigestInputStream, MessageDigest}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.utils.URLEncodedUtils
import Predef._


object Utils {

  val UTF8_ENCODING = "UTF-8"
  val SHA_256 = "SHA-256"
  val HMAC_SHA_256 = "HmacSHA256"

  def toHex(data: Array[Byte]): String = {
    val sb = new collection.mutable.StringBuilder(data.length * 2)
    var i = 0
    while(i < data.length) {

      val hex: String = Integer.toHexString(data(i))
      if(hex.length == 1) {
        sb.append("0").append(hex)
      } else if(hex.length == 8) {
        sb.append(hex.substring(6))
      } else {
        sb.append(hex)
      }
      i += 1
    }
    sb.toString().toLowerCase(Locale.getDefault)
  }

  def hash(data: Array[Byte]): Array[Byte] = {
    val md: MessageDigest = MessageDigest.getInstance(SHA_256)
    md.update(data)
    md.digest
  }

  def hash(text: String): Array[Byte] = {
    val md: MessageDigest = MessageDigest.getInstance(SHA_256)
    md.update(text.getBytes(UTF8_ENCODING))
    md.digest
  }

  def hash(input: InputStream): Array[Byte] = {
    val md: MessageDigest = MessageDigest.getInstance(SHA_256)
    val digestInputStream: DigestInputStream = new DigestInputStream(input, md)
    val buffer: Array[Byte] = new Array[Byte](1024)
    while (digestInputStream.read(buffer) > -1) {}
    digestInputStream.getMessageDigest.digest
  }

  def hmac(data: Array[Byte], key: Array[Byte]): Array[Byte] = {
    val mac: Mac = Mac.getInstance(HMAC_SHA_256)
    mac.init(new SecretKeySpec(key, HMAC_SHA_256))
    mac.doFinal(data)
  }

  def hmac(data: String, key: Array[Byte]): Array[Byte] = {
    hmac(data.getBytes, key)
  }

  def hmac(data: Array[Byte], key: String): Array[Byte] = {
    hmac(data, key.getBytes)
  }

  def hmac(data: String, key: String): Array[Byte] = {
    hmac(data.getBytes, key.getBytes)
  }

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

  object caseInsensitiveOrder extends Ordering[String] {
    def compare(x: String, y: String): Int =  String.CASE_INSENSITIVE_ORDER.compare(x, y)
  }

  def sortCaseIns(entities: Traversable[(String, String)]): scala.collection.immutable.TreeMap[String, String] = {
    new scala.collection.immutable.TreeMap[String, String]()(caseInsensitiveOrder) ++ entities
  }

  def sort(entities: Traversable[(String, String)]): scala.collection.immutable.TreeMap[String, String] = {
    new scala.collection.immutable.TreeMap[String, String]() ++ entities
  }



  def getCanonicalizedQueryString(sortedParameters: Map[String, String]): String = {

    val builder = new java.lang.StringBuilder()

    sortedParameters.foreach { case (key, value) =>
      if (builder.length > 0) {
        builder.append("&")
      }
      builder.append(Utils.urlEncode(key, false))
      builder.append("=")
      builder.append(Utils.urlEncode(value, false))
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
      byteArrayOutputStream.close()
      content2.reset()
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

  def getRequestPayloadWithoutQueryParams(content: Array[Byte]): String = {
    new String(content, "UTF-8")
  }


  def base64Encode(data: Array[Byte]): String = {
    new String(Base64.encodeBase64(data))
  }

  def urlEncode(value: String, path: Boolean): String = {
    if (value == null) {
      ""
    } else {
      val encoded: String = URLEncoder.encode(value, UTF8_ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
      if (path) {
        encoded.replace("%2F", "/")
      } else {
        encoded
      }
    }
  }

  def encodeParameters(parameters: Traversable[(String, String)]): Option[String] = {
    val nameValuePairs = new util.ArrayList[NameValuePair](parameters.size)
    parameters.foreach{ case (key, value) =>
      nameValuePairs.add(new BasicNameValuePair(key, value))
    }

    if (!nameValuePairs.isEmpty) {
      Some(URLEncodedUtils.format(nameValuePairs, UTF8_ENCODING))
    } else {
      None
    }
  }
}
