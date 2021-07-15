import Dependencies._
import com.typesafe.sbt.SbtGit.git
import java.nio.file.{Files, Paths}
import java.io.File

inThisBuild(
  List(
    organization := "ai.kien",
    homepage := Some(url("https://github.com/kiendang/python-native-libs")),
    licenses := List("BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")),
    developers := List(
      Developer(
        "kiendang",
        "Dang Trung Kien",
        "mail@kien.ai",
        url("https://kien.ai")
      )
    )
  )
)

lazy val scala212 = "2.12.14"
lazy val scala213 = "2.13.6"
lazy val scala3   = "3.0.0"

ThisBuild / scalaVersion := scala213

lazy val root = (project in file("."))
  .settings(
    name := "Python Native Libs",
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies ++= Seq(
      scalaCollectionCompat,
      scalaTest % Test,
      jimfs     % Test
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )

lazy val docs = project
  .in(file("python-docs"))
  .settings(
    mdocVariables := Map(
      "VERSION"         -> "0.1.2",
      "SCALAPY_VERSION" -> "0.5.0",
      "PYTHON"          -> "/usr/bin/python3"
    )
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
