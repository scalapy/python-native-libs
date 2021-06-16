import Dependencies._

ThisBuild / organization := "ai.kien"

lazy val scala212 = "2.12.14"
lazy val scala213 = "2.13.6"

ThisBuild / scalaVersion := scala213

lazy val root = (project in file("."))
  .settings(
    name := "Python Native Libs",
    crossScalaVersions := Seq(scala212, scala213),
    libraryDependencies ++= Seq(
      scalalCollectionCompat,
      scalaTest % Test,
      jimfs % Test
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )

lazy val docs = project
  .in(file("python-docs"))
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value
    )
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
