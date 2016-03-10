Nice.scalaProject

name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"
scalaVersion := "2.11.8"
crossScalaVersions := Seq(scalaVersion.value, "2.10.6")


val sdkVersion = "1.10.59"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-sns"         % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-sqs"         % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-autoscaling" % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-s3"          % sdkVersion,
  "com.amazonaws" % "aws-java-sdk-ec2"         % sdkVersion,
  "org.scalatest" %% "scalatest"               % "2.2.6"     % Test
)

// FIXME: warts should be turned on back after the code is cleaned up
wartremoverErrors in (Compile, compile) := Seq()
