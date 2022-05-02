import Dependencies._

inThisBuild(
  List(
    organization := "ai.kien",
    homepage     := Some(url("https://github.com/kiendang/python-native-libs")),
    licenses     := List("BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")),
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

lazy val scala212 = "2.12.15"
lazy val scala213 = "2.13.8"
lazy val scala3   = "3.1.0"

lazy val scalapyVersion = getProp("plugin.scalapy.version").getOrElse("0.5.2")

ThisBuild / scalaVersion := scala213

ThisBuild / scalafixDependencies += organizeImports

def getProp(p: String) = Option(sys.props(p)).map(_.trim).filter(_.nonEmpty)

def getProps(prop: String*) =
  prop
    .map(p => p -> getProp(p))
    .collect { case (k, Some(v)) => s"""-D$k=$v""" }

lazy val publishSettings = Seq(
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository     := "https://s01.oss.sonatype.org/service/local"
)

lazy val noPublishSettings = Seq(
  publishArtifact   := false,
  packagedArtifacts := Map.empty,
  publish           := {},
  publishLocal      := {}
)

lazy val root = project
  .in(file("."))
  .settings(
    name               := "Python Native Libs",
    crossScalaVersions := Seq(scala212, scala213, scala3),
    libraryDependencies += scalaCollectionCompat,
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
  .settings(publishSettings)

lazy val tests = project
  .in(file("tests"))
  .enablePlugins(ScriptedPlugin)
  .settings(
    scalaVersion := scala212,
    scriptedLaunchOpts ++= {
      Seq(s"-Dplugin.scalapy.version=$scalapyVersion") ++
        getProps("plugin.python.executable", "plugin.virtualenv") ++
        Seq("-Xmx1024M", "-Dplugin.version=" + (root / version).value)
    },
    scriptedBufferLog := false
  )
  .settings(noPublishSettings)

lazy val docs = project
  .in(file("python-docs"))
  .enablePlugins(MdocPlugin)
  .settings(
    mdocVariables := Map(
      "PYTHON" -> "/usr/bin/python3"
    )
  )
  .dependsOn(root)
