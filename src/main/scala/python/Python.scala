package python

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

  def existsInPath(exec: String): Boolean = path
    .split(pathSeparator)
    .toStream
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

  lazy val interp: Try[String] =
    interpreter.map(Success(_)).getOrElse(python)

  private def callPython(cmd: String): Try[String] =
    interp.flatMap(python => callProcess0(Seq(python, "-c", cmd)))

  private def ldversion: Try[String] =
    callPython("import sysconfig;print(sysconfig.get_config_var('LDVERSION'))")

  lazy val nativeLibPaths: Try[Seq[String]] =
    callPython("import sys;print(sys.exec_prefix)")
      .map(_ + "/lib")
      .map(Seq(_))

  lazy val library = ldversion.map("python" + _)

  private def scalapyPropsTry: Try[Map[String, String]] = for {
    nativeLibPaths <- nativeLibPaths
    library <- library
  } yield {
    val paths = nativeLibPaths.mkString(pathSeparator)
    val currentPaths = Properties.propOrEmpty("jna.library.path")
    val newPaths =
      if (currentPaths contains paths) currentPaths
      else Seq(paths, currentPaths).mkString(pathSeparator)

    Map("jna.library.path" -> newPaths, "scalapy.python.library" -> library)
  }

  private def currentProps: Map[String, String] =
    LazyList("jna.library.path", "scalapy.python.library")
      .map(p => p -> Properties.propOrEmpty(p))
      .toMap

  def scalapyProps: Map[String, String] =
    currentProps ++ scalapyPropsTry.getOrElse(Map.empty)
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
