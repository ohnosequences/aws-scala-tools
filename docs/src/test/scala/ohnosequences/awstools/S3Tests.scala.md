
```scala
// package ohnosequences.awstools.s3
//
// import org.junit.Test
// import org.junit.Assert._
//
// import ohnosequences.awstools.test.awsClients
// import java.io.File
// import ohnosequences.awstools.s3._
// import com.amazonaws.services.s3.transfer.TransferManager
//
// class S3Tests {
//
//   @Test
//   def loadingManager() = {
//     val s3: S3 = awsClients.s3
//
//     val bucket = "ohnosequences-awstools-test"
//
//     s3.createBucket(bucket)
//     val file = new File("build.sbt")
//     val objectAddress = S3Object(bucket, file.getName)
//
//
//     val loadManager = s3.createLoadingManager()
//     loadManager.upload(objectAddress, file)
//
//     val tmpFile = File.createTempFile("test", "file")
//     println("created temp file: " + tmpFile.getAbsolutePath)
//     loadManager.download(objectAddress, tmpFile)
//
//     val content1 = scala.io.Source.fromFile(file).mkString
//     val content2 = scala.io.Source.fromFile(tmpFile).mkString
//
//     tmpFile.delete()
//     s3.deleteBucket(bucket)
//
//     assertEquals(content1, content2)
//   }
//
// }

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../../../../main/scala/ohnosequences/awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/address.scala]: ../../../../main/scala/ohnosequences/awstools/s3/address.scala.md
[main/scala/ohnosequences/awstools/s3/client.scala]: ../../../../main/scala/ohnosequences/awstools/s3/client.scala.md
[main/scala/ohnosequences/awstools/s3/package.scala]: ../../../../main/scala/ohnosequences/awstools/s3/package.scala.md
[main/scala/ohnosequences/awstools/s3/transfers.scala]: ../../../../main/scala/ohnosequences/awstools/s3/transfers.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../../../../main/scala/ohnosequences/awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../../../../main/scala/ohnosequences/awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../../../main/scala/ohnosequences/benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../../../main/scala/ohnosequences/logging/Logger.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: SQSTests.scala.md