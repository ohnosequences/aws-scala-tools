
```scala
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