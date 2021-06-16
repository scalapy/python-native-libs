import sbt._

object Dependencies {
  lazy val scalalCollectionCompat = "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.4"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.8"
  lazy val jimfs = "com.google.jimfs" % "jimfs" % "1.2"
}
