//package ohnosequences.saws.signing.v3
//
//import java.text.SimpleDateFormat
//import java.util.{Date, SimpleTimeZone, Locale}
//import java.net.URL
//import java.io.InputStream
//
//trait V3Data[R] {
//  def getHeaders(r: R): Map[String, String]
//  def getParameters(r: R): Map[String, String]
//  def getResourcePath(r: R): String
//  def getMethod(r: R): String
//  def getEndpoint(r: R): String
//  def getContent(r: R): InputStream
//
//  def getAdditionalHeaders(r: R): Map[String, String] = {
//    val rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
//    rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"))
//    val date: String = rfc822DateFormat.format(new Date())
//    val hostHeader: String = new URL(getEndpoint(r)).getHost
//    Map[String, String](
//      "Host" -> hostHeader,
//      "Date" -> date,
//      "X-Amz-Date" -> date
//    )
//  }
//}