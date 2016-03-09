package ohnosequences.logging

import java.io.{FileWriter, PrintWriter, File}
import java.nio.file.{StandardCopyOption, Files}
import java.text.SimpleDateFormat
import java.util.Date

import ohnosequences.awstools.s3._
import ohnosequences.benchmark.Bench


import scala.annotation.tailrec
import scala.sys.process.ProcessLogger
import scala.util.{Success, Failure, Try}


trait Logger { logger =>

  val prefix: String

  def processLogger: ProcessLogger = new ProcessLogger {

    override def buffer[T](f: => T): T = f

    override def out(s: => String): Unit = info(s)

    override def err(s: => String): Unit = error(s)

  }

  def warnP(s: String, prefix: Option[String]): Unit

  def warnP(t: Throwable, prefix: Option[String]): Unit =  {
    printThrowable(t, {s => warnP(s, prefix)})
  }

  def warn(s: String): Unit = warnP(s, Some(logger.prefix))

  def warn(t: Throwable): Unit =  warnP(t, Some(logger.prefix))

  def subLogger(prefix: String): Logger

  def errorP(t: Throwable, prefix: Option[String], maxDepth: Int = 5, stackThreshold: Int = 10): Unit =  {
    printThrowable(t, {s => errorP(s, prefix)}, maxDepth, stackThreshold)
  }

  def errorP(s: String, prefix: Option[String]): Unit

  def error(t: Throwable, maxDepth: Int = 5, stackThreshold: Int = 10): Unit = errorP(t, Some(logger.prefix), maxDepth, stackThreshold)

  def error(s: String): Unit = errorP(s, Some(logger.prefix))

  def info(s: String, prefix: Option[String] = Some(logger.prefix)): Unit

  def debugP(s: String, prefix: Option[String]): Unit

  def debugP(t: Throwable, prefix: Option[String], maxDepth: Int = 5, stackThreshold: Int = 10): Unit =  {
    printThrowable(t, {s => debugP(s, prefix)}, maxDepth, stackThreshold)
  }

  def debug(s: String): Unit = debugP(s, Some(logger.prefix))

  def debug(t: Throwable, maxDepth: Int = 5, stackThreshold: Int = 10): Unit =  debugP(t, Some(logger.prefix), maxDepth, stackThreshold)

  def uploadFile(file: File, workingDirectory: File): Try[Unit]

  def printThrowable(t: Throwable, print: String => Unit, maxDepth: Int = 5, stackThreshold: Int = 10): Unit = {

    @tailrec
    def printThrowableRec(t: Throwable, depth: Int): Unit = {
      if (depth > maxDepth) {
        ()
      } else {
        print(t.toString)
        t.getStackTrace.take(stackThreshold).foreach { s =>
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

  override val prefix: String = "unit"

  override def warnP(s: String, prefix: Option[String]) {}

  override def errorP(s: String, prefix: Option[String]) {}

  override def info(s: String, prefix: Option[String]) {}

  override def debugP(s: String, prefix: Option[String]) {}

  override def uploadFile(file: File, workingDirectory: File): Try[Unit] = {Success(())}

  override def subLogger(prefix: String): Logger = unitLogger
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


class ConsoleLogger(val prefix: String, debug: Boolean = false) extends Logger {

  rootLogger =>

  val formatter = new LogFormatter(prefix)

  override def toString: String = "ConsoleLogger[" + prefix + "]"


  override def info(s: String, prefix: Option[String] = Some(rootLogger.prefix)) {
    println(formatter.info(s, prefix))
  }

  override def errorP(s: String, prefix: Option[String]) {
    println(formatter.error(s, prefix))
  }

  override def warnP(s: String, prefix: Option[String]) {
    println(formatter.warn(s, prefix))
  }


  override def debugP(s: String, prefix: Option[String]): Unit = {
    if (debug) {
      println(formatter.debug(s, prefix))
    }
  }

 // override def copy(prefix: String): ConsoleLogger = new ConsoleLogger(prefix, debug)

  override def uploadFile(file: File, workingDirectory: File): Try[Unit] = {
    info("uploading " + file.getAbsolutePath)
    warn("uploading is not implemented in ConsoleLogger")
    Success(())
  }

  override def subLogger(suffix: String): ConsoleLogger =
    new ConsoleLogger(prefix + "/" + suffix, debug)

}

object FileLogger {
  def apply(prefix: String,
            loggingDirectory: File,
            logFileName: String,
            debug: Boolean,
            printToConsole: Boolean = true): Try[FileLogger] = {
    Try {
      loggingDirectory.mkdir()
      new FileLogger(prefix, loggingDirectory, logFileName, debug, printToConsole, None)
    }.recoverWith { case t =>
      Failure(new Error("failed to create logging file: " + t, t))
    }
  }
}

class FileLogger(val prefix: String,
                 val loggingDirectory: File,
                 logFileName: String,
                 debug: Boolean,
                 printToConsole: Boolean = true,
                 original: Option[FileLogger]) extends Logger { fileLogger =>

  val formatter = new LogFormatter(prefix)
  val logFile = new File(loggingDirectory, logFileName)
  val log = new PrintWriter(new FileWriter(logFile), true)


  val consoleLogger = if (printToConsole) {
    Some(new ConsoleLogger(prefix, debug))
  } else {
    None
  }

  override def warnP(s: String, prefix: Option[String]) {
    original.foreach {
      _.warnP(s, prefix)
    }

    consoleLogger.foreach {
      _.warnP(s, prefix)
    }

    log.println(formatter.warn(s, prefix))
   // log.flush()
  }

  override def errorP(s: String, prefix: Option[String]): Unit = {

    original.foreach {
      _.errorP(s, prefix)
    }

    consoleLogger.foreach {
      _.errorP(s, prefix)
    }
    log.println(formatter.error(s, prefix))
    //log.flush()
  }


  override def toString: String = "FileLogger[" + prefix + "]"

  override def info(s: String, prefix: Option[String] = Some(fileLogger.prefix)): Unit = {

    original.foreach {
      _.info(s, prefix)
    }

    consoleLogger.foreach {
      _.info(s, prefix)
    }
    log.println(formatter.info(s, prefix))
   // log.flush()
  }

  override def debugP(s: String, prefix: Option[String]): Unit = {

    if (debug) {
      original.foreach {
        _.debugP(s, prefix)
      }
      consoleLogger.foreach {
        _.debugP(s, prefix)
      }
      log.println(formatter.debug(s, prefix))
    //  log.flush()
    }
  }

  // override def copy(prefix: String): FileLogger = new FileLogger(prefix, logFile, debug, printToConsole)

  override def subLogger(suffix: String): FileLogger = {
    val newDirectory = new File(loggingDirectory, suffix)
    newDirectory.mkdir()

    new FileLogger(
      prefix + "/" + suffix,
      newDirectory,
      logFileName,
      debug,
      printToConsole = false,
      Some(fileLogger)
    )
  }

  override def uploadFile(file: File, workingDirectory: File): Try[Unit] = {
    Try {
      val path = file.getAbsolutePath.replace(workingDirectory.getAbsolutePath, "")
      Files.copy(file.toPath, new File(loggingDirectory, path).toPath, StandardCopyOption.REPLACE_EXISTING)
    }
  }
}


object S3Logger {
  def apply(s3: S3,
            prefix: String,
            loggingDirectory: File,
            logFileName: String,
            loggingDestination: Option[S3Folder],
            debug: Boolean,
            printToConsole: Boolean = true): Try[S3Logger] = {
    Try {
      loggingDirectory.mkdir()
      val logFile = new File(loggingDirectory, logFileName)
      new S3Logger(s3, prefix, loggingDirectory, logFileName, loggingDestination, debug, printToConsole, None)
    }.recoverWith { case t =>
      Failure(new Error("failed to create logging file: " + t, t))
    }
  }
}

class S3Logger(s3: S3,
               prefix: String,
               loggingDirectory: File,
               logFileName: String,
               val loggingDestination: Option[S3Folder],
               debug: Boolean,
               printToConsole: Boolean = true,
               original: Option[S3Logger]) extends FileLogger(prefix, loggingDirectory, logFileName, debug, printToConsole, original) {
  s3Logger =>

  override def toString: String = "S3Logger[" + prefix + "]"

  def uploadLog(): Try[Unit] = {
    Try {
      loggingDestination.foreach { dst =>
       // log.flush()
        s3.s3.createBucket(dst.bucket)
        s3.createLoadingManager.upload(dst / logFileName, logFile)
      }
    }
  }

  override def uploadFile(file: File, zeroDir: File): Try[Unit] =  {
    Try {
      loggingDestination.foreach { dst =>
        val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
        s3.s3.createBucket(dst.bucket)
        s3.createLoadingManager.upload(dst / path, file)
      }
    }
  }


  override def subLogger(suffix: String): S3Logger = {
    val newDirectory = new File(loggingDirectory, suffix)
    newDirectory.mkdir()

    new S3Logger(s3, prefix + "/" + suffix,  new File(loggingDirectory, suffix),
      logFileName, loggingDestination,  debug, printToConsole = false, Some(s3Logger))
  }

  def subLogger(suffix: String, loggingDestination: Option[S3Folder]): S3Logger = {
    val newDirectory = new File(loggingDirectory, suffix)
    newDirectory.mkdir()

    new S3Logger(s3, prefix + "/" + suffix,  new File(loggingDirectory, suffix),
      logFileName, loggingDestination,  debug, printToConsole = false, Some(s3Logger))
  }

}
