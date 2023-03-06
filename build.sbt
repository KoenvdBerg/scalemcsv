ThisBuild / scalaVersion := "3.2.2"
ThisBuild / organization := "com.github.scalemcsv"
ThisBuild / version := "1.0.0-SNAPSHOT"
name := "scalemcsv"
crossPaths := false


lazy val root = (project in file("."))
  .settings(
    name := "scalemcsv",
  )



libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.10"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies +="org.scalatest" %% "scalatest" % "3.2.9" % Test
libraryDependencies += "dev.zio" %% "zio" % "2.0.8"
libraryDependencies += "com.lihaoyi" %% "mainargs" % "0.4.0"
libraryDependencies += "org.apache.poi" % "poi" % "3.17"
libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.17"
