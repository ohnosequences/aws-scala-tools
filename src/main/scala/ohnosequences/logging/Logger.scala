package ohnosequences.logging

import java.io.{PrintWriter, File}
import java.text.SimpleDateFormat
import java.util.Date

import ohnosequences.awstools.s3.{ObjectAddress, S3}
import ohnosequences.benchmark.Bench

import scala.annotation.tailrec


trait Logger {

  def warn(s: String): Unit

//  def copy(prefix: String): Logger

  def subLogger(prefix: String): Logger

  def warn(t: Throwable): Unit =  {
    printThrowable(t, warn)
  }

  def error(t: Throwable): Unit =  {
    printThrowable(t, error)
  }

  def error(s: String): Unit

  def info(s: String): Unit

  def debug(s: String): Unit

  def printThrowable(t: Throwable, print: String => Unit, maxDepth: Int = 5): Unit = {
    
    @tailrec
    def printThrowableRec(t: Throwable, depth: Int): Unit = {
      if (depth > maxDepth) {
        ()
      } else {
        print(t.toString)
        t.getStackTrace.foreach { s =>
          print("    at " + s.toString)
        }
        Option(t.getCause) match {
          case None => ()
          case Some(cause) => {
            print("Caused by:")
            printThrowableRec(cause, depth+1)}
        } 
      }
    }
    printThrowableRec(t, 1)
  }

  def debug(t: Throwable): Unit =  {
    printThrowable(t, debug)
  }


  def benchExecute[T](description: String, bench: Option[Bench] = None)(statement: =>T): T = {
    val t1 = System.currentTimeMillis()
    val res = statement
    val t2 = System.currentTimeMillis()
    debug(description + " finished in " + (t2 - t1) + " ms")
    bench.foreach(_.register(description, t2 - t1))
    res
  }

}

object unitLogger extends Logger {
  override def warn(s: String) {}

  override def error(s: String) {}

  override def info(s: String) {}

  override def debug(s: String) {}

 // override def copy(prefix: String): Logger = unitLogger

  override def subLogger(prefix: String): Logger = unitLogger
}



class LogFormatter(prefix: String) {

  val format = new SimpleDateFormat("HH:mm:ss.SSS")

  def pref(): String = {
    format.format(new Date()) + " " + prefix
  }


  def info(s: String): String = {
    "[" + "INFO  " + pref + "]: " + s
  }

  def error(s: String): String = {
    "[" + "ERROR " + pref + "]: " + s
  }

  def warn(s: String): String =  {
    "[" + "WARN  " + pref + "]: " + s
  }

  def debug(s: String): String =  {
    "[" + "DEBUG " + pref + "]: " + s
  }
}


class ConsoleLogger(prefix: String, debug: Boolean = false) extends Logger {

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

  override def debug(s: String): Unit = {
    if (debug) {
      println(formatter.debug(s))
    }
  }

 // override def copy(prefix: String): ConsoleLogger = new ConsoleLogger(prefix, debug)

  override def subLogger(suffix: String): ConsoleLogger = new ConsoleLogger(prefix + "/" + suffix, debug)

}

class FileLogger(prefix: String,
                 val loggingDirectory: File,
                 logFileName: String,
                 debug: Boolean,
                 printToConsole: Boolean = true) extends Logger {

  val formatter = new LogFormatter(prefix)

  loggingDirectory.mkdir()

  val logFile = new File(loggingDirectory, logFileName)

  val log = new PrintWriter(logFile)

  val consoleLogger = if (printToConsole) {
    Some(new ConsoleLogger(prefix, debug))
  } else {
    None
  }

  override def warn(s: String) {
    consoleLogger.foreach {
      _.warn(s)
    }
    log.println(formatter.warn(s))
    log.flush()
  }

  override def error(s: String): Unit = {
    consoleLogger.foreach {
      _.error(s)
    }
    log.println(formatter.error(s))
    log.flush()
  }

  override def info(s: String): Unit = {
    consoleLogger.foreach {
      _.info(s)
    }
    log.println(formatter.info(s))
    log.flush()
  }

  override def debug(s: String): Unit = {
    if (debug) {
      consoleLogger.foreach {
        _.debug(s)
      }
      log.println(formatter.debug(s))
      log.flush()
    }
  }

  // override def copy(prefix: String): FileLogger = new FileLogger(prefix, logFile, debug, printToConsole)

  override def subLogger(suffix: String): FileLogger = {
    new FileLogger(
      prefix + "/" + suffix,
      new File(loggingDirectory, suffix),
      logFileName,
      debug,
      printToConsole
    )
  }
}


class S3Logger(s3: S3,
               prefix: String,
               workingDirectory: File,
               logFileName: String,
               bucketName: String,
               debug: Boolean,
               printToConsole: Boolean = true) extends FileLogger(prefix, workingDirectory, logFileName, debug, printToConsole) {

  def uploadLog(): Unit = {
    log.flush()
    s3.putObject(ObjectAddress(bucketName, prefix.hashCode.toString) / prefix / logFileName, logFile)
  }

  def uploadFile(file: File, zeroDir: File = workingDirectory) {
    val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
    s3.putObject(ObjectAddress(bucketName, prefix.hashCode.toString) / prefix / path, file)
  }


  override def subLogger(suffix: String): S3Logger = {
    new S3Logger(s3, prefix + "/" + suffix, new File(workingDirectory, suffix),
      logFileName, bucketName,  debug, printToConsole)
  }

}


