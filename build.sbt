Nice.scalaProject

name := "aws-scala-tools"

description := "AWS Scala tools"

organization := "ohnosequences"

bucketSuffix := "era7.com"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.7.7",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)
