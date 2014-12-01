Nice.scalaProject

name := "aws-scala-tools"

description := "AWS Scala tools"

organization := "ohnosequences"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq(
//  "com.amazonaws" % "aws-java-sns" % "1.9.8",
//  "com.amazonaws" % "aws-java-sqs" % "1.9.8",
//  "com.amazonaws" % "aws-java-s3" % "1.9.8",
//  "com.amazonaws" % "aws-java-ec2" % "1.9.8",
//  "com.amazonaws" % "aws-java-iam" % "1.9.8",
  "com.amazonaws" % "aws-java-sdk" % "1.8.2",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)
