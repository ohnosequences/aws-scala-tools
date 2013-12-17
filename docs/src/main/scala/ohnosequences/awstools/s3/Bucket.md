### Index

+ src
  + main
    + scala
      + ohnosequences
        + awstools
          + autoscaling
            + [AutoScaling.scala](../autoscaling/AutoScaling.md)
            + [AutoScalingGroup.scala](../autoscaling/AutoScalingGroup.md)
          + cloudwatch
            + [CloudWatch.scala](../cloudwatch/CloudWatch.md)
          + dynamodb
            + [DynamoDB.scala](../dynamodb/DynamoDB.md)
            + [DynamoObjectMapper.scala](../dynamodb/DynamoObjectMapper.md)
          + ec2
            + [EC2.scala](../ec2/EC2.md)
            + [Filters.scala](../ec2/Filters.md)
            + [InstanceType.scala](../ec2/InstanceType.md)
            + [Utils.scala](../ec2/Utils.md)
          + s3
            + [Bucket.scala](Bucket.md)
            + [S3.scala](S3.md)
          + sns
            + [SNS.scala](../sns/SNS.md)
            + [Topic.scala](../sns/Topic.md)
          + sqs
            + [Queue.scala](../sqs/Queue.md)
            + [SQS.scala](../sqs/SQS.md)
  + test
    + scala
      + ohnosequences
        + awstools
          + [DynamoDBTests.scala](../../../../../test/scala/ohnosequences/awstools/DynamoDBTests.md)
          + [EC2Tests.scala](../../../../../test/scala/ohnosequences/awstools/EC2Tests.md)
          + [S3Tests.scala](../../../../../test/scala/ohnosequences/awstools/S3Tests.md)
          + [SNSTests.scala](../../../../../test/scala/ohnosequences/awstools/SNSTests.md)
          + [SQSTests.scala](../../../../../test/scala/ohnosequences/awstools/SQSTests.md)

------


```scala
package ohnosequences.awstools.s3

import com.amazonaws.services.s3.AmazonS3
import java.io.File
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}

case class Bucket(s3: AmazonS3, name: String) {

  def putObject(file: File, public: Boolean = false) {
    if (public) {
      s3.putObject(new PutObjectRequest(name, file.getName, file).withCannedAcl(CannedAccessControlList.PublicRead))
    } else {
      s3.putObject(new PutObjectRequest(name, file.getName, file))
    }
  }

//  def putObject(inputStream: InsputStream, public: Boolean = false) {
//    if (public) {
//      s3.putObject(new PutObjectRequest(name, file.getName, file).withCannedAcl(CannedAccessControlList.PublicRead))
//    } else {
//      s3.putObject(new PutObjectRequest(name, file.getName, file))
//    }
//  }

//  def putObject(key: String, s: String, public: Boolean = false) {
//    var putRequest = new PutObjectRequest(name, key, IOUtils.toInputStream(s))
//  }

  def delete {
    s3.deleteBucket(name)
  }



//  def getUrl = {
//    s3.getBucketLocation(name)
//  }
}

```

