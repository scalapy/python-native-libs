import ai.kien.python.Python

lazy val scala212 = "2.12.14"
lazy val scala213 = "2.13.6"
lazy val scala3   = "3.0.0"

ThisBuild / scalaVersion := scala213

lazy val scalapyVersion = System.getenv("CI_SCALAPY_VERSION")

lazy val python = Python(
  Option(System.getenv("CI_PYTHONEXECUTABLE")).filter(_.trim.nonEmpty)
)

lazy val pythonLdFlags = python.ldflags.get

lazy val javaOpts = python.scalapyProperties.get.map { case (k, v) =>
  s"""-D$k=$v"""
}.toSeq

lazy val root = crossProject(JVMPlatform, NativePlatform)
  .in(file("."))
  .settings(
    crossScalaVersions := Seq(scala212, scala213)
  )
  .jvmSettings(
    fork := true,
    javaOptions ++= javaOpts,
    libraryDependencies += "me.shadaj" %% "scalapy-core" % scalapyVersion
  )
  .nativeSettings(
    libraryDependencies += "me.shadaj" %%% "scalapy-core" % scalapyVersion,
    nativeLinkStubs := true,
    nativeLinkingOptions ++= pythonLdFlags
  )
