import Dependencies._

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

lazy val enableScripted = Option(sys.props("plugin.ci")).map(_.trim.nonEmpty).getOrElse(false)

ThisBuild / scalaVersion := (if (enableScripted) scala212 else scala213)

ThisBuild / scalafixDependencies += organizeImports

def warnUnusedImports(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, _)) => Seq("-Xlint:unused")
    case _            => Nil
  }

def scriptedPlugin = if (enableScripted) Seq(ScriptedPlugin) else Nil

def scriptedSettings = if (enableScripted) {
  Seq(
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++ {
        Seq(s"-Dplugin.scalapy.version=$scalapyVersion") ++
          Option(sys.props("plugin.python.executable"))
            .map("-Dplugin.python.executable=" + _)
            .toSeq ++
          Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
      }
    },
    scriptedBufferLog := false
  )
} else Nil

lazy val scalapyVersion = Option(sys.props("plugin.scalapy.version")).map(_.trim).getOrElse("0.5.0")

lazy val root = (project in file("."))
  .enablePlugins(scriptedPlugin: _*)
  .settings(
    name := "Python Native Libs",
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies ++= Seq(
      scalaCollectionCompat,
      scalaTest % Test,
      jimfs     % Test
    ),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= warnUnusedImports(scalaVersion.value)
  )
  .settings(scriptedSettings)

lazy val docs = project
  .in(file("python-docs"))
  .settings(
    mdocVariables := Map(
      "VERSION"         -> "0.1.3",
      "SCALAPY_VERSION" -> scalapyVersion,
      "PYTHON"          -> "/usr/bin/python3"
    )
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
