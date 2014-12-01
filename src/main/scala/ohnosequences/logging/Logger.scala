package ohnosequences.logging

import java.io.{PrintWriter, File}
import java.text.SimpleDateFormat
import java.util.Date

import ohnosequences.awstools.s3.{ObjectAddress, S3}


trait Logger {

  def warn(s: String): Unit

  def warn(t: Throwable): Unit =  { warn(t.toString) }

  def error(t: Throwable): Unit =  { error(t.toString) }

  def error(s: String): Unit

  def info(s: String): Unit

  def benchExecute[T](description: String)(statement: =>T): T = {
    val t1 = System.currentTimeMillis()
    val res = statement

    val t2 = System.currentTimeMillis()
    info(description + " finished in " + (t2 - t1) + " ms")
    res
  }
}

object unitLogger extends Logger {
  override def warn(s: String) {}

  override def error(s: String) {}

  override def info(s: String) {}
}



class LogFormatter(prefix: String) {

  val format = new SimpleDateFormat("HH:mm:ss.SSS")

  def pref(): String = {
    format.format(new Date()) + " " + prefix + ": "
  }


  def info(s: String): String = {
    "[" + "INFO " + prefix + "]: " + s
  }

  def error(s: String): String = {
    "[" + "ERROR " + prefix + "]: " + s
  }

  def warn(s: String): String =  {
    "[" + "WARN " + prefix + "]: " + s
  }
}


class ConsoleLogger(prefix: String) extends Logger {

  val formatter = new LogFormatter(prefix)

  override def info(s: String) {
    println(formatter.info(s))
  }

  override def error(s: String) {
    println(formatter.error(s))
  }

  override def warn(s: String) {
    println(formatter.warn(s))
  }

  override def error(t: Throwable): Unit = {
    error(t.toString)
    t.printStackTrace()
  }

  override def warn(t: Throwable): Unit = {
    warn(t.toString)
  //  t.printStackTrace()
  }
}

class FileLogger(prefix: String, workingDir: File = new File("."), printToConsole: Boolean = true) extends Logger {

  val formatter = new LogFormatter(prefix)

  val logFile = new File(workingDir, "log.txt")
  val log = new PrintWriter(logFile)

  val consoleLogger = if (printToConsole) {
    Some(new ConsoleLogger(prefix))
  } else {
    None
  }

  override def warn(s: String) {
    consoleLogger.foreach{_.warn(s)}
    log.println(formatter.warn(s))
  }

  override def error(s: String): Unit = {
    consoleLogger.foreach{_.error(s)}
    log.println(formatter.error(s))
  }

  override def info(s: String): Unit = {
    consoleLogger.foreach{_.info(s)}
    log.println(formatter.info(s))
  }

}

class S3Logger(s3: S3, prefix: String, workingDir: File, printToConsole: Boolean = true) extends FileLogger(prefix, workingDir, printToConsole) {
  def uploadLog( destination: ObjectAddress): Unit = {
    log.close()
    s3.putObject(destination, logFile)
  }

  def uploadFile(destination: ObjectAddress, file: File, zeroDir: File = workingDir) {
    val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
    s3.putObject(destination / path, file)
  }

}


