package ohnosequences.logging

import java.io.{PrintWriter, File}
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.Date

import ohnosequences.awstools.s3.{ObjectAddress, S3}
import ohnosequences.benchmark.Bench


import scala.annotation.tailrec
import scala.sys.process.ProcessLogger
import scala.util.{Failure, Try}


trait Logger {

  def processLogger: ProcessLogger = new ProcessLogger {

    override def buffer[T](f: => T): T = f

    override def out(s: => String): Unit = info(s)

    override def err(s: => String): Unit = error(s)

  }

  def warnP(s: String, prefix: Option[String]): Unit

  def warnP(t: Throwable, prefix: Option[String]): Unit =  {
    printThrowable(t, {s => warnP(s, prefix)})
  }

  def warn(s: String): Unit = warnP(s, None)

  def warn(t: Throwable): Unit =  warnP(t, None)

  def subLogger(prefix: String, reportToOriginal: Boolean): Logger


  def errorP(t: Throwable, prefix: Option[String]): Unit =  {
    printThrowable(t, {s => errorP(s, prefix)})
  }

  def errorP(s: String, prefix: Option[String]): Unit

  def error(t: Throwable): Unit = errorP(t, None)

  def error(s: String): Unit = errorP(s, None)

  def info(s: String, prefix: Option[String] = None): Unit

  def debugP(s: String, prefix: Option[String]): Unit

  def debugP(t: Throwable, prefix: Option[String]): Unit =  {
    printThrowable(t, {s => debugP(s, prefix)})
  }

  def debug(s: String): Unit = debugP(s, None)

  def debug(t: Throwable): Unit =  debugP(t, None)


  def uploadFile(file: File, workingDirectory: File): Unit

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
  override def warnP(s: String, prefix: Option[String]) {}

  override def errorP(s: String, prefix: Option[String]) {}

  override def info(s: String, prefix: Option[String]) {}

  override def debugP(s: String, prefix: Option[String]) {}

  override def uploadFile(file: File, workingDirectory: File): Unit = {}

  override def subLogger(prefix: String,  reportToOriginal: Boolean): Logger = unitLogger
}



class LogFormatter(prefix: String) {

  val format = new SimpleDateFormat("HH:mm:ss.SSS")

  def pref(p: Option[String]): String = {
    format.format(new Date()) + " " + (p match {
      case None => prefix
      case Some(s) => s
    })
  }


  def info(s: String, prefix: Option[String] = None): String = {
    "[" + "INFO  " + pref(prefix) + "]: " + s
  }

  def error(s: String, prefix: Option[String] = None): String = {
    "[" + "ERROR " + pref(prefix) + "]: " + s
  }

  def warn(s: String, prefix: Option[String] = None): String =  {
    "[" + "WARN  " + pref(prefix) + "]: " + s
  }

  def debug(s: String, prefix: Option[String] = None): String =  {
    "[" + "DEBUG " + pref(prefix) + "]: " + s
  }
}


class ConsoleLogger(prefix: String, debug: Boolean = false, originalLogger: Option[ConsoleLogger] = None) extends Logger {

  rootLogger =>

  val formatter = new LogFormatter(prefix)

  override def info(s: String, prefix: Option[String] = None) {

    originalLogger.foreach{ l =>
      l.info(s, Some(rootLogger.prefix))
    }

    println(formatter.info(s, prefix))
  }

  override def errorP(s: String, prefix: Option[String]) {
    originalLogger.foreach(_.errorP(s, Some(rootLogger.prefix)))

    println(formatter.error(s, prefix))
  }

  override def warnP(s: String, prefix: Option[String]) {
    originalLogger.foreach(_.warnP(s, Some(rootLogger.prefix)))
    println(formatter.warn(s, prefix))
  }


  override def debugP(s: String, prefix: Option[String]): Unit = {
    if (debug) {
      originalLogger.foreach(_.debugP(s, Some(rootLogger.prefix)))
      println(formatter.debug(s, prefix))
    }
  }

 // override def copy(prefix: String): ConsoleLogger = new ConsoleLogger(prefix, debug)

  override def uploadFile(file: File, workingDirectory: File): Unit = {
    info("uploading " + file.getAbsolutePath)
    warn("uploading is not implemented in ConsoleLogger")
  }

  override def subLogger(suffix: String, reportToOriginal: Boolean): ConsoleLogger =
    new ConsoleLogger(prefix + "/" + suffix, debug,
      if (reportToOriginal) Some(ConsoleLogger.this) else None
    )

}

object FileLogger {
  def apply(prefix: String,
            loggingDirectory: File,
            logFileName: String,
            debug: Boolean,
            printToConsole: Boolean = true): Try[FileLogger] = {
    Try {
      loggingDirectory.mkdir()
      val logFile = new File(loggingDirectory, logFileName)
      new FileLogger(prefix, loggingDirectory, logFileName, debug, printToConsole, None)
    }.recoverWith { case t =>
      Failure(new Error("failed to create logging file: " + t, t))
    }
  }
}

class FileLogger(prefix: String,
                 val loggingDirectory: File,
                 logFileName: String,
                 debug: Boolean,
                 printToConsole: Boolean = true,
                 original: Option[FileLogger]) extends Logger { rootLogger =>

  val formatter = new LogFormatter(prefix)
  val logFile = new File(loggingDirectory, logFileName)
  val log = new PrintWriter(logFile)

  val consoleLogger = if (printToConsole && original.isEmpty) {
    Some(new ConsoleLogger(prefix, debug, None))
  } else {
    None
  }

  override def warnP(s: String, prefix: Option[String]) {
    original.foreach {
      _.warnP(s, Some(rootLogger.prefix))
    }
    consoleLogger.foreach {
      _.warnP(s, prefix)
    }
    log.println(formatter.warn(s, prefix))
    log.flush()
  }

  override def errorP(s: String, prefix: Option[String]): Unit = {
    original.foreach {
      _.errorP(s, Some(rootLogger.prefix))
    }
    consoleLogger.foreach {
      _.errorP(s, prefix)
    }
    log.println(formatter.error(s, prefix))
    log.flush()
  }

  override def info(s: String, prefix: Option[String] = None): Unit = {
    original.foreach {
      //println("reporting to original: " + s)
      _.info(s, Some(rootLogger.prefix))
    }
    consoleLogger.foreach {
      _.info(s, prefix)
    }
    log.println(formatter.info(s, prefix))
    log.flush()
  }

  override def debugP(s: String, prefix: Option[String]): Unit = {

    if (debug) {
      original.foreach {
        _.debugP(s, Some(rootLogger.prefix))
      }
      consoleLogger.foreach {
        _.debugP(s, prefix)
      }
      log.println(formatter.debug(s, prefix))
      log.flush()
    }
  }

  // override def copy(prefix: String): FileLogger = new FileLogger(prefix, logFile, debug, printToConsole)

  override def subLogger(suffix: String, reportToOriginal: Boolean): FileLogger = {
    val newDirectory = new File(loggingDirectory, suffix)
    newDirectory.mkdir()

    new FileLogger(
      prefix + "/" + suffix,
      newDirectory,
      logFileName,
      debug,
      printToConsole,
      if(reportToOriginal) Some(FileLogger.this) else None
    )
  }

  override def uploadFile(file: File, workingDirectory: File): Unit = {
    val path = file.getAbsolutePath.replace(workingDirectory.getAbsolutePath, "")
    Files.copy(file.toPath, new File(loggingDirectory, path).toPath)
  }
}


object S3Logger {
  def apply(s3: S3,
            prefix: String,
            loggingDirectory: File,
            logFileName: String,
            bucketName: String,
            debug: Boolean,
            printToConsole: Boolean = true): Try[S3Logger] = {
    Try {
      loggingDirectory.mkdir()
      val logFile = new File(loggingDirectory, logFileName)
      new S3Logger(s3, prefix, loggingDirectory, logFileName, bucketName, debug, printToConsole, None)
    }.recoverWith { case t =>
      Failure(new Error("failed to create logging file: " + t, t))
    }
  }
}

class S3Logger(s3: S3,
               prefix: String,
               loggingDirectory: File,
               logFileName: String,
               bucketName: String,
               debug: Boolean,
               printToConsole: Boolean = true,
               original: Option[S3Logger] ) extends FileLogger(prefix, loggingDirectory, logFileName, debug, printToConsole, original) {
  rootLogger =>
  def uploadLog(): Unit = {
    log.flush()
    s3.putObject(ObjectAddress(bucketName, prefix.hashCode.toString) / prefix / logFileName, logFile)
  }

  override def uploadFile(file: File, zeroDir: File) {
    val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
    s3.putObject(ObjectAddress(bucketName, prefix.hashCode.toString) / prefix / path, file)
  }


  override def subLogger(suffix: String, reportOriginal: Boolean): S3Logger = {
    new S3Logger(s3, prefix + "/" + suffix,  new File(loggingDirectory, suffix),
      logFileName, bucketName,  debug, printToConsole,
      if (reportOriginal) Some(S3Logger.this) else None
    )
  }

}


