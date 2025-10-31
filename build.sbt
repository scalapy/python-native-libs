import Dependencies._

inThisBuild(
  List(
    organization := "ai.kien",
    homepage     := Some(url("https://github.com/kiendang/python-native-libs")),
    licenses     := List("BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")),
    developers   := List(
      Developer(
        "kiendang",
        "Dang Trung Kien",
        "mail@kien.ai",
        url("https://kien.ai")
      )
    )
  )
)

lazy val scala212 = "2.12.20"
lazy val scala213 = "2.13.17"
lazy val scala3   = "3.3.7"

ThisBuild / scalaVersion := scala213

lazy val root = project
  .in(file("."))
  .settings(
    name               := "Python Native Libs",
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies ++= Seq(
      scalaCollectionCompat,
      scalapy   % Test,
      scalaTest % Test
    ),
    Test / fork := true
  )
  .settings(
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions += {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 13)) => "-Wunused:imports"
        case Some((2, 12)) => "-Ywarn-unused-import"
        case _             => ""
      }
    }
  )

lazy val docs = project
  .in(file("python-docs"))
  .enablePlugins(MdocPlugin)
  .settings(
    mdocVariables := Map(
      "PYTHON" -> "/usr/bin/python3"
    )
  )
  .dependsOn(root)
