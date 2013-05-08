
import sbtrelease._
import ReleaseStateTransformations._

import awstoolsBuild._

name := "aws-scala-tools"

organization := "ohnosequences"

version := "0.2.3"

scalaVersion := "2.10.0"

<<<<<<< HEAD
//crossScalaVersions := Seq("2.9.1")

=======
>>>>>>> 59dcc0fc477693fde13b7996a4825796879eb2d5
publishMavenStyle := true

publishTo <<= version { (v: String) =>
  if (v.trim.endsWith("SNAPSHOT"))
    Some(Resolver.file("local-snapshots", file("artifacts/snapshots.era7.com")))
  else
    Some(Resolver.file("local-releases", file("artifacts/releases.era7.com")))
}

resolvers ++= Seq (
                    "Typesafe Releases"   at "http://repo.typesafe.com/typesafe/releases",
                    "Sonatype Releases"   at "https://oss.sonatype.org/content/repositories/releases",
                    "Sonatype Snapshots"  at "https://oss.sonatype.org/content/repositories/snapshots",
                    "Era7 Releases"       at "http://releases.era7.com.s3.amazonaws.com",
                    "Era7 Snapshots"      at "http://snapshots.era7.com.s3.amazonaws.com"
                  )

libraryDependencies ++= Seq (
                              "com.amazonaws" % "aws-java-sdk" % "1.3.26"
                            , "com.novocode" % "junit-interface" % "0.10-M1" % "test"
                            )

scalacOptions ++= Seq(
                      "-feature",
                      "-language:higherKinds",
                      "-language:implicitConversions",
                      "-deprecation",
                      "-unchecked"
                    )
