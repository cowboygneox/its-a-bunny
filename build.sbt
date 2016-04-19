name := """its-a-bunny"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test,
  "com.udojava" % "EvalEx" % "1.0"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
