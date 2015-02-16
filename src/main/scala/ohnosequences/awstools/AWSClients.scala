package ohnosequences.awstools

import com.amazonaws.auth.{AWSCredentialsProvider}
import ohnosequences.awstools.ec2.EC2
import ohnosequences.awstools.autoscaling.AutoScaling
import ohnosequences.awstools.sqs.SQS
import ohnosequences.awstools.sns.SNS
import ohnosequences.awstools.s3.S3
import ohnosequences.awstools.regions.Region.Ireland
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient

trait AWSClients {
  val ec2: EC2
  val as: AutoScaling
  val sqs: SQS
  val sns: SNS
  val s3: S3
  val ddb: AmazonDynamoDBClient
}

object AWSClients {
  def create(credentialsProvider: AWSCredentialsProvider, region: ohnosequences.awstools.regions.Region = Ireland) = new AWSClients {
    val ec2 = EC2.create(credentialsProvider, region)
    val as = AutoScaling.create(credentialsProvider, ec2, region)
    val sqs = SQS.create(credentialsProvider, region)
    val sns = SNS.create(credentialsProvider, region)
    val s3 = S3.create(credentialsProvider, region)
    val ddb = new AmazonDynamoDBClient(credentialsProvider)
    ddb.setRegion(region.toAWSRegion)
  }
}

