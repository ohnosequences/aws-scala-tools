Nice.scalaProject

name := "aws-scala-tools"

description := "AWS Scala tools"

organization := "ohnosequences"

bucketSuffix := "frutero.org"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.7.3",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)
