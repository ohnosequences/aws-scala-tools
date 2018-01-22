name := "aws-scala-tools"
organization := "ohnosequences"
description := "AWS Scala tools"

bucketSuffix := "era7.com"

crossScalaVersions := Seq("2.11.12", "2.12.4")
scalaVersion := crossScalaVersions.value.max

// https://github.com/aws/aws-sdk-java/blob/master/CHANGELOG.md
val sdkVersion = "1.11.268"

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
  "org.scalatest" %% "scalatest" % "3.0.4" % Test
)

wartremoverWarnings in (Compile, compile) --= Seq(
  Wart.Throw,
  Wart.DefaultArguments
)
wartremoverErrors in (Test, compile) --= Seq(
  Wart.TryPartial
)

parallelExecution in Test := false
