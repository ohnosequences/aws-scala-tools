resolvers ++= Seq (
                        "Era7 Releases"       at "http://releases.era7.com.s3.amazonaws.com",
                        "Era7 Snapshots"      at "http://snapshots.era7.com.s3.amazonaws.com"
                      )


addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8")

addSbtPlugin("ohnosequences" % "sbt-s3-resolver" % "0.6.0")
