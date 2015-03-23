Nice.scalaProject

name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"
scalaVersion := "2.11.6"
crossScalaVersions := Seq("2.10.5", "2.11.6")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.9.25",
//  "com.amazonaws" % "aws-java-sns" % "1.9.25",
//  "com.amazonaws" % "aws-java-sqs" % "1.9.25",
//  "com.amazonaws" % "aws-java-s3" % "1.9.25",
//  "com.amazonaws" % "aws-java-ec2" % "1.9.25",
//  "com.amazonaws" % "aws-java-iam" % "1.9.25",
  "com.novocode"  % "junit-interface" % "0.11" % Test
)
