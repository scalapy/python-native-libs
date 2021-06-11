package ai.kien.python

import java.io.{File, FileNotFoundException}
import java.nio.file.{FileSystem, FileSystems, Files}
import scala.sys
import scala.sys.process.Process
import scala.util.{Properties, Success, Try}

class Python private (
    interpreter: Option[String],
    callProcess: Seq[(String, String)] => Seq[String] => Try[String],
    env: Map[String, String],
    fs: FileSystem,
    isWindows: Boolean
) {
  private val callProcess0 = callProcess(env.toSeq) andThen (_.map(_.trim))

  private val path: String = env.get("PATH").getOrElse("")

  private val pathSeparator = if (isWindows) ";" else File.pathSeparator

  private def existsInPath(exec: String): Boolean = path
    .split(pathSeparator)
    .to(LazyList)
    .map(fs.getPath(_))
    .exists(path => Files.exists(path.resolve(exec)))

  private lazy val python: Try[String] = Try(
    if (existsInPath("python3"))
      "python3"
    else if (existsInPath("python"))
      "python"
    else
      throw new FileNotFoundException(
        "Neither python3 nor python was found in $PATH."
      )
  )

  private lazy val interp: Try[String] =
    interpreter.map(Success(_)).getOrElse(python)

  private def callPython(cmd: String*): Try[String] =
    interp.flatMap(python => callProcess0(Seq(python, "-c", cmd.mkString(";"))))

  private def ldversion: Try[String] =
    callPython("import sysconfig;print(sysconfig.get_config_var('LDVERSION'))")

  lazy val nativeLibraryPaths: Try[Seq[String]] =
    callPython(
      "import sys",
      "import sysconfig",
      "print(sysconfig.get_config_var('LIBPL') + ';')",
      "print(sys.prefix + '/lib;')",
      "print(sys.exec_prefix + '/lib;')"
    ).map(_.split(";")).map(_.map(_.trim).distinct)

  lazy val nativeLibrary: Try[String] = ldversion.map("python" + _)

  def scalaPyProperties: Try[Map[String, String]] = for {
    nativeLibPaths <- nativeLibraryPaths
    library <- nativeLibrary
  } yield {
    val currentPathsStr = Properties.propOrEmpty("jna.library.path")
    val currentPaths = currentPathsStr.split(pathSeparator)
    val pathsToAdd =
      if (currentPaths.containsSlice(nativeLibPaths)) Nil else nativeLibPaths
    val newPaths =
      pathsToAdd.mkString(pathSeparator) +
      (if (currentPathsStr == "") "" else pathSeparator + currentPathsStr)

    Map("jna.library.path" -> newPaths, "scalapy.python.library" -> library)
  }
}

object Python {
  private def callProcess(env: Seq[(String, String)])(cmd: Seq[String]) =
    Try(Process(cmd, None, env: _*).!!)

  def apply(interpreter: Option[String] = None): Python =
    new Python(
      interpreter,
      callProcess,
      sys.env,
      FileSystems.getDefault(),
      false
    )

  def apply(interpreter: String): Python = apply(Some(interpreter))
}
