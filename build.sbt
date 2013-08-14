
import sbtrelease._
import ReleaseStateTransformations._

import awstoolsBuild._

name := "aws-scala-tools"

organization := "ohnosequences"

version := "0.2.5"

scalaVersion := "2.10.2"

publishMavenStyle := true

s3credentialsFile in Global := Some("/home/evdokim/era7.prop")


publishTo <<= (isSnapshot, s3credentials) {
                (snapshot,   credentials) =>
  val prefix = if (snapshot) "snapshots" else "releases"
  credentials map s3resolver("Era7 "+prefix+" S3 bucket", "s3://"+prefix+".era7.com")
}

resolvers ++= Seq (
                    "Typesafe Releases"   at "http://repo.typesafe.com/typesafe/releases",
                    "Sonatype Releases"   at "https://oss.sonatype.org/content/repositories/releases",
                    "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots",
                    "Era7 Releases"       at "http://releases.era7.com.s3.amazonaws.com",
                    "Era7 Snapshots"      at "http://snapshots.era7.com.s3.amazonaws.com"
                  )

libraryDependencies ++= Seq (
                              "com.amazonaws" % "aws-java-sdk" % "1.5.1"
                            , "com.novocode" % "junit-interface" % "0.10-M1" % "test"
                            )

scalacOptions ++= Seq(
                      "-feature",
                      "-language:higherKinds",
                      "-language:implicitConversions",
                      "-deprecation",
                      "-unchecked"
                    )
