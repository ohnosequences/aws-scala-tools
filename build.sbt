Nice.scalaProject

name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"
scalaVersion := "2.11.7"
crossScalaVersions := Seq("2.10.5", scalaVersion.value)


libraryDependencies ++= Seq(
//  "com.amazonaws" % "aws-java-sdk" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-sns" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-sqs" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-autoscaling" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-ec2" % "1.10.9",
  "com.amazonaws" % "aws-java-sdk-iam" % "1.10.9",
  "com.novocode"  % "junit-interface" % "0.11" % Test
)
