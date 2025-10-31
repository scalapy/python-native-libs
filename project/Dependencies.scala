import sbt._

object Dependencies {
  lazy val scalaCollectionCompat =
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.12.0"
  lazy val scalapy   = "dev.scalapy"   %% "scalapy-core" % "0.5.3"
  lazy val scalaTest = "org.scalatest" %% "scalatest"    % "3.2.19"
}
