
```scala
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



```


------

### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]
            + [AutoScalingGroup.scala][main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]
          + cloudwatch
            + [CloudWatch.scala][main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]
          + dynamodb
            + [DynamoDB.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]
            + [DynamoObjectMapper.scala][main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/dynamodb/Utils.scala]
          + ec2
            + [EC2.scala][main/scala/ohnosequences/awstools/ec2/EC2.scala]
            + [Filters.scala][main/scala/ohnosequences/awstools/ec2/Filters.scala]
            + [InstanceType.scala][main/scala/ohnosequences/awstools/ec2/InstanceType.scala]
            + [Utils.scala][main/scala/ohnosequences/awstools/ec2/Utils.scala]
          + regions
            + [Region.scala][main/scala/ohnosequences/awstools/regions/Region.scala]
          + s3
            + [Bucket.scala][main/scala/ohnosequences/awstools/s3/Bucket.scala]
            + [S3.scala][main/scala/ohnosequences/awstools/s3/S3.scala]
          + sns
            + [SNS.scala][main/scala/ohnosequences/awstools/sns/SNS.scala]
            + [Topic.scala][main/scala/ohnosequences/awstools/sns/Topic.scala]
          + sqs
            + [Queue.scala][main/scala/ohnosequences/awstools/sqs/Queue.scala]
            + [SQS.scala][main/scala/ohnosequences/awstools/sqs/SQS.scala]
        + logging
          + [Logger.scala][main/scala/ohnosequences/logging/Logger.scala]
          + [S3Logger.scala][main/scala/ohnosequences/logging/S3Logger.scala]
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala][test/scala/ohnosequences/awstools/DynamoDBTests.scala]
          + [EC2Tests.scala][test/scala/ohnosequences/awstools/EC2Tests.scala]
          + [InstanceTypeTests.scala][test/scala/ohnosequences/awstools/InstanceTypeTests.scala]
          + [RegionTests.scala][test/scala/ohnosequences/awstools/RegionTests.scala]
          + [S3Tests.scala][test/scala/ohnosequences/awstools/S3Tests.scala]
          + [SNSTests.scala][test/scala/ohnosequences/awstools/SNSTests.scala]
          + [SQSTests.scala][test/scala/ohnosequences/awstools/SQSTests.scala]

[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/cloudwatch/CloudWatch.scala]: ../awstools/cloudwatch/CloudWatch.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDB.scala]: ../awstools/dynamodb/DynamoDB.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoObjectMapper.scala]: ../awstools/dynamodb/DynamoObjectMapper.scala.md
[main/scala/ohnosequences/awstools/dynamodb/Utils.scala]: ../awstools/dynamodb/Utils.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/Utils.scala]: ../awstools/ec2/Utils.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/Bucket.scala]: ../awstools/s3/Bucket.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../awstools/s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: S3Logger.scala.md
[test/scala/ohnosequences/awstools/DynamoDBTests.scala]: ../../../../test/scala/ohnosequences/awstools/DynamoDBTests.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/InstanceTypeTests.scala]: ../../../../test/scala/ohnosequences/awstools/InstanceTypeTests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SNSTests.scala]: ../../../../test/scala/ohnosequences/awstools/SNSTests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md