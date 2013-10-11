
name := "aws-scala-tools"

organization := "ohnosequences"

version := "0.4.0-SNAPSHOT"

scalaVersion := "2.10.2"

resolvers ++= Seq (
  "Typesafe Releases"   at "http://repo.typesafe.com/typesafe/releases",
  "Sonatype Releases"   at "https://oss.sonatype.org/content/repositories/releases",
  "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots",
  "Era7 Releases"       at "http://releases.era7.com.s3.amazonaws.com",
  "Era7 Snapshots"      at "http://snapshots.era7.com.s3.amazonaws.com"
)

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.6.1",
  "com.novocode" % "junit-interface" % "0.10" % "test",
  "org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-jdk14" % "1.7.5"
)

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-deprecation",
  "-unchecked"
)
