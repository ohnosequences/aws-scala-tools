
name := "aws-scala-tools"

organization := "ohnosequences"

Nice.scalaProject

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.6.5",
  "com.novocode" % "junit-interface" % "0.10" % "test"
)


