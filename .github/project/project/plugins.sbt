addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.1.0")
addSbtPlugin("org.scala-native"   % "sbt-scala-native"              % "0.4.0")
libraryDependencies += "ai.kien" %% "python-native-libs"            % System.getenv("CI_LIBRARY_VERSION")
