package ai.kien.python

import scala.util.Try

private[python] class PythonConfig(
    pythonConfig: String,
    callProcess: Seq[String] => Try[String] = Defaults.callProcess
) {
  def callPythonConfig(cmd: String*): Try[String] = callProcess(pythonConfig +: cmd)

  lazy val help = callPythonConfig("--help")

  lazy val isEmbed = help.map(_.contains("--embed"))

  lazy val ldflags: Try[String] = for {
    isEmbed <- isEmbed
    opts = "--ldflags" +: (if (isEmbed) Seq("--embed") else Nil)
    ldflags <- callPythonConfig(opts: _*)
  } yield ldflags
}
