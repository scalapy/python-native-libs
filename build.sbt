import Dependencies._

// ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "ai.kien"

lazy val scala212 = "2.12.14"
lazy val scala213 = "2.13.6"

ThisBuild / scalaVersion := scala213

lazy val root = (project in file("."))
  .settings(
    name := "Python Native Libs",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      jimfs % Test
    )
  )

lazy val docs = project
  .in(file("python-docs"))
  .settings(
    crossScalaVersions := Seq(scala212, scala213),
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
