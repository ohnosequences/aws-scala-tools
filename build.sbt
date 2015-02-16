Nice.scalaProject

name := "aws-scala-tools"

description := "AWS Scala tools"

organization := "ohnosequences"

bucketSuffix := "era7.com"

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.11.4")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-sns" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-sqs" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-ec2" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-iam" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.9.19",
  "com.amazonaws" % "aws-java-sdk-autoscaling" % "1.9.19",
  "com.novocode" % "junit-interface" % "0.11" % "test"
)
