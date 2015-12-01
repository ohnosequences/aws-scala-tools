
```scala
// package ohnosequences.awstools.ec2
//
// import org.junit.Test
// import org.junit.Assert._
//
// import com.amazonaws.services.ec2.model.LaunchSpecification
//
// import java.util.Arrays
// import ohnosequences.awstools.ec2.InstanceType._
//
//
// class EC2Tests {
//
//   @Test
//   def instanceSpecs() {
//     val specs = InstanceSpecs(instanceType = t1.micro, keyName = "keyName", securityGroups = List("sg1"), amiId = "amiId1")
//     assertEquals("amiId1", specs.amiId)
//     assertEquals(t1.micro, specs.instanceType)
//     assertEquals(List("sg1"), specs.securityGroups)
//
//     val lspecs: LaunchSpecification = specs
//     assertEquals("amiId1", lspecs.getImageId)
//     assertEquals(t1.micro.toString, lspecs.getInstanceType)
//     assertEquals(Arrays.asList("sg1"), lspecs.getSecurityGroups)
//
//     val specsWitUserData: LaunchSpecification = InstanceSpecs(instanceType = t1.micro, keyName = "keyName", securityGroups = List("sg1"), amiId = "amiId1", userData = "test test")
//     assertEquals(Utils.base64encode("test test"), specsWitUserData.getUserData)
//   }
//
//   @Test
//   def base64Tests() {
//     assertEquals("dGVzdHRlc3QK", Utils.base64encode("testtest\n"))
//   }
//
//
//
// }

```




[main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScaling.scala.md
[main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/AutoScalingGroup.scala.md
[main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/LaunchConfiguration.scala.md
[main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala]: ../../../../main/scala/ohnosequences/awstools/autoscaling/PurchaseModel.scala.md
[main/scala/ohnosequences/awstools/AWSClients.scala]: ../../../../main/scala/ohnosequences/awstools/AWSClients.scala.md
[main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/dynamodb/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/ec2/AMI.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/AMI.scala.md
[main/scala/ohnosequences/awstools/ec2/EC2.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/EC2.scala.md
[main/scala/ohnosequences/awstools/ec2/Filters.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/Filters.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/InstanceType.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/InstanceType.scala.md
[main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/LaunchSpecs.scala.md
[main/scala/ohnosequences/awstools/ec2/package.scala]: ../../../../main/scala/ohnosequences/awstools/ec2/package.scala.md
[main/scala/ohnosequences/awstools/regions/Region.scala]: ../../../../main/scala/ohnosequences/awstools/regions/Region.scala.md
[main/scala/ohnosequences/awstools/s3/S3.scala]: ../../../../main/scala/ohnosequences/awstools/s3/S3.scala.md
[main/scala/ohnosequences/awstools/sns/SNS.scala]: ../../../../main/scala/ohnosequences/awstools/sns/SNS.scala.md
[main/scala/ohnosequences/awstools/sns/Topic.scala]: ../../../../main/scala/ohnosequences/awstools/sns/Topic.scala.md
[main/scala/ohnosequences/awstools/sqs/Queue.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/Queue.scala.md
[main/scala/ohnosequences/awstools/sqs/SQS.scala]: ../../../../main/scala/ohnosequences/awstools/sqs/SQS.scala.md
[main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/AutoScalingUtils.scala.md
[main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/DynamoDBUtils.scala.md
[main/scala/ohnosequences/awstools/utils/SQSUtils.scala]: ../../../../main/scala/ohnosequences/awstools/utils/SQSUtils.scala.md
[main/scala/ohnosequences/benchmark/Benchmark.scala]: ../../../../main/scala/ohnosequences/benchmark/Benchmark.scala.md
[main/scala/ohnosequences/logging/Logger.scala]: ../../../../main/scala/ohnosequences/logging/Logger.scala.md
[main/scala/ohnosequences/logging/S3Logger.scala]: ../../../../main/scala/ohnosequences/logging/S3Logger.scala.md
[test/scala/ohnosequences/awstools/AWSClients.scala]: AWSClients.scala.md
[test/scala/ohnosequences/awstools/EC2Tests.scala]: EC2Tests.scala.md
[test/scala/ohnosequences/awstools/RegionTests.scala]: RegionTests.scala.md
[test/scala/ohnosequences/awstools/S3Tests.scala]: S3Tests.scala.md
[test/scala/ohnosequences/awstools/SQSTests.scala]: SQSTests.scala.md