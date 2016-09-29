name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"
scalaVersion := "2.11.8"

val sdkVersion = "1.11.38"

val services = Seq(
  "autoscaling",
  "ec2",
  "s3",
  "sns",
  "sqs"
)

libraryDependencies ++= services.map { service =>
  "com.amazonaws" % s"aws-java-sdk-${service}" % sdkVersion
} ++ Seq(
  "org.scalatest" %% "scalatest" % "2.2.6" % Test
)

// FIXME: warts should be turned on back after the code is cleaned up
wartremoverErrors in (Compile, compile) := Seq()
