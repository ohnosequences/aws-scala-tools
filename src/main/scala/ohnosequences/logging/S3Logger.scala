//package ohnosequences.logging
//
//import ohnosequences.awstools.s3.ObjectAddress
//import ohnosequences.nisperon.{NisperonConfiguration, AWS}
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//
////todo add verbose level
////todo add upload manager here
//class S3Logger(prefix: String, aws: AWS, destination: ObjectAddress, workingDir: String) extends Logger {
//  val buffer = new StringBuilder
////  def uploadFile(file: File, zeroDir: File = new File(workingDir)) {
////    val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
////    aws.s3.putObject(destination / path, file)
////  }
////
////  def uploadLog(destination: ObjectAddress) {
////    val r = buffer.toString()
////    if(!r.isEmpty) {
////      aws.s3.putWholeObject(destination, buffer.toString())
////    }
////  }
//
//  def pref(): String = {
//    format.format(new Date()) + " " + prefix + ": "
//  }
//
//  val format = new SimpleDateFormat("HH:mm:ss.SSS")
//
//  def warn(s: String) {
//    val ss = pref() + " WARN: " + s + System.lineSeparator()
//    println(ss)
//    buffer.append(ss)
//  }
//
//  def error(s: String) {
//    val ss = pref() + " ERROR: " + s + System.lineSeparator()
//    println(ss)
//    buffer.append(ss)
//  }
//
//  def info(s: String) {
//    val ss = pref() + " INFO: " + s + System.lineSeparator()
//    println(ss)
//    buffer.append(ss)
//  }
//
//
//
//}

