import sbt._

object Dependencies {
  lazy val scalaCollectionCompat =
    "org.scala-lang.modules" %% "scala-collection-compat" % "2.7.0"
  lazy val scalaTest       = "org.scalatest"        %% "scalatest"        % "3.2.12"
  lazy val jimfs           = "com.google.jimfs"      % "jimfs"            % "1.2"
  lazy val organizeImports = "com.github.liancheng" %% "organize-imports" % "0.5.0"
}
