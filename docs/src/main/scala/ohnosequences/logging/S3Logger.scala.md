
```scala
//package ohnosequences.logging
//
//import ohnosequences.awstools.s3.S3Object
//import ohnosequences.nisperon.{NisperonConfiguration, AWS}
//import java.io.File
//import java.text.SimpleDateFormat
//import java.util.Date
//
////todo add verbose level
////todo add upload manager here
//class S3Logger(prefix: String, aws: AWS, destination: S3Object, workingDir: String) extends Logger {
//  val buffer = new StringBuilder
////  def uploadFile(file: File, zeroDir: File = new File(workingDir)) {
////    val path = file.getAbsolutePath.replace(zeroDir.getAbsolutePath, "")
////    aws.s3.putObject(destination / path, file)
////  }
////
////  def uploadLog(destination: S3Object) {
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




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../awstools/autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../awstools/autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../awstools/AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../awstools/dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../awstools/ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../awstools/ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../awstools/ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../awstools/ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../awstools/s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../awstools/utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../awstools/utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../awstools/utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../test/scala/ohnosequences/awstools/AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: ../../../../test/scala/ohnosequences/awstools/EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: ../../../../test/scala/ohnosequences/awstools/RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: ../../../../test/scala/ohnosequences/awstools/S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: ../../../../test/scala/ohnosequences/awstools/SQSTests.scala.md