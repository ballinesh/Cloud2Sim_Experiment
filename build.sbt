import sbt.project
name := "homework1"

version := "0.1"

scalaVersion := "2.13.1"

//resolvers += Resolver.sbtPluginRepo("releases")

mainClass in (Compile,run) := Some ("HW1")

//libraryDependencies += "ch.qos.logback" % "logback-core" % "1.3.0-alpha4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.3.0-alpha4" % Test
libraryDependencies += "ch.qos.logback" % "logback-examples" % "1.3.0-alpha4"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.8.0-beta1"
//libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"
libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.8"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"