ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "scalemcsv"
  )

libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"
//libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies +="org.scalatest" %% "scalatest" % "3.2.9" % Test
