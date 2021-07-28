package ai.kien.python

import scala.util.Try
import scala.sys.process.Process

private[python] object Defaults {
  def callProcess(cmd: Seq[String]) = Try(Process(cmd).!!)

  def getEnv(k: String) = Option(System.getenv(k))
}
