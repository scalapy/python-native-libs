package ai.kien.python

import java.io.{File, FileNotFoundException}
import java.nio.file.{FileSystem, FileSystems, Files}
import scala.collection.compat.immutable.LazyList
import scala.sys
import scala.sys.process.Process
import scala.util.{Properties, Success, Try}

class Python private[python] (
    interpreter: Option[String] = None,
    callProcess: Seq[(String, String)] => Seq[String] => Try[String] = Python.callProcess,
    env: Map[String, String] = sys.env,
    fs: FileSystem = FileSystems.getDefault,
    isWindows: Option[Boolean] = None
) {
  private val callProcess0 = callProcess(env.toSeq) andThen (_.map(_.trim))

  private val path: String = env.get("PATH").getOrElse("")

  private val pathSeparator = isWindows.map(if (_) ";" else ":").getOrElse(File.pathSeparator)

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

  private def callPython(cmd: String): Try[String] =
    interp.flatMap(python => callProcess0(Seq(python, "-c", cmd)))

  private def ldversion: Try[String] = callPython(Python.ldversionCmd)

  lazy val nativeLibraryPaths: Try[Seq[String]] =
    callPython(Python.libPathCmd)
      .map(_.split(";"))
      .map(_.map(_.trim).distinct.filter(_.nonEmpty).toSeq)

  lazy val nativeLibrary: Try[String] = ldversion.map("python" + _)

  lazy val executable: Try[String] = callPython(Python.executableCmd)

  def scalapyProperties: Try[Map[String, String]] = for {
    nativeLibPaths <- nativeLibraryPaths
    library        <- nativeLibrary
    executable     <- executable
  } yield {
    val currentPathsStr = Properties.propOrEmpty("jna.library.path")
    val currentPaths    = currentPathsStr.split(pathSeparator)

    val pathsToAdd =
      if (currentPaths.containsSlice(nativeLibPaths)) Nil else nativeLibPaths
    val pathsToAddStr = pathsToAdd.mkString(pathSeparator)

    val newPaths = (currentPathsStr, pathsToAddStr) match {
      case (c, p) if c.isEmpty => p
      case (c, p) if p.isEmpty => c
      case (c, p)              => s"$p$pathSeparator$c"
    }

    Map(
      "jna.library.path"           -> newPaths,
      "scalapy.python.library"     -> library,
      "scalapy.python.programname" -> executable
    )
  }
}

object Python {
  def apply(interpreter: Option[String] = None): Python =
    new Python(interpreter, callProcess)

  def apply(interpreter: String): Python = apply(Some(interpreter))

  private def callProcess(env: Seq[(String, String)])(cmd: Seq[String]) =
    Try(Process(cmd, None, env: _*).!!)

  private def executableCmd = "import sys;print(sys.executable)"

  private def ldversionCmd =
    "import sys,sysconfig;print(sysconfig.get_python_version() + sys.abiflags)"

  private def libPathCmd = Seq(
    "import sys",
    "from sysconfig import get_config_var",
    "print(get_config_var('LIBPL') + ';')",
    "print(sys.base_prefix + '/lib')"
  ).mkString(";")
}
