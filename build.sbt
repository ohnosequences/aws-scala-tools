Nice.scalaProject

name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"
scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.10.5", scalaVersion.value)


val sdkVersion = "1.10.32"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-sns"         % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-sqs"         % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-autoscaling" % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-dynamodb"    % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-s3"          % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-ec2"         % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-iam"         % sdkVersion,
  "org.scalatest" %% "scalatest"               % "2.2.5"     % Test
)

// FIXME: warts should be turn on back after the code clean up
wartremoverErrors in (Compile, compile) := Seq()
