import sbt._

object Dependencies {
  lazy val scalaCollectionCompat =
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.4"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.9"
  lazy val jimfs = "com.google.jimfs" % "jimfs" % "1.2"
}
