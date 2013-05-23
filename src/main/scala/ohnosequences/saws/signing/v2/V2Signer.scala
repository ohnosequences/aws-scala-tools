package ohnosequences.saws.signing.v2

import ohnosequences.saws.signing.{Utils, Credentials, SigningProcess}
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

object V2SigningProcess extends SigningProcess(v2) {
  def apply(input: V2Input, credentials: Credentials): Map[String, String] = {
    val endpoint = input.endpoint
    val resource = input.resource

    val df: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    df.setTimeZone(TimeZone.getTimeZone("UTC"))
    val timestamp = df.format(new Date())


    val additionalParameters = Map[String, String](
      "AWSAccessKeyId" -> credentials.accessKey,
      "SignatureVersion" -> "2",
      "Timestamp" -> timestamp,
      "SignatureMethod" -> Utils.HMAC_SHA_256
    )

    val parameters = Utils.sort(input.parameters) ++ additionalParameters

    //calculateStringToSignV2(request);
    val builder = new StringBuilder()
    builder.append("POST").append("\n")
    builder.append(endpoint).append("\n")
    builder.append(Utils.getCanonicalizedResourcePath(resource)).append("\n")
    builder.append(Utils.getCanonicalizedQueryString(parameters))

    val stringToSign = builder.toString()

   // println("v2  : string to sign: " + stringToSign)

    val signature = Utils.base64Encode(Utils.hmac(stringToSign, credentials.secretKey))

   // println("v2  : signature:" + signature)
    val result = additionalParameters + ("Signature" -> signature)

    result
  }
}
