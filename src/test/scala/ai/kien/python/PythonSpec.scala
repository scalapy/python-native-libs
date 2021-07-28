package ai.kien.python

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import com.google.common.jimfs.{Configuration, Jimfs}
import scala.util.{Properties, Success, Try}

class PythonSpec extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {
  val ldversionCmd  = Python invokePrivate PrivateMethod[String](Symbol("ldversionCmd"))()
  val libPathCmd    = Python invokePrivate PrivateMethod[String](Symbol("libPathCmd"))()
  val executableCmd = Python invokePrivate PrivateMethod[String](Symbol("executableCmd"))()

  val ldversion        = "3.9"
  val base_prefix      = "/usr/local"
  val base_exec_prefix = "/usr/local"
  val prefix           = "/home/user/.envs/env"
  val exec_prefix      = "/home/user/.envs/env"
  val libpl            = s"$base_exec_prefix/python$ldversion/config-$ldversion"
  val executable       = s"$prefix/bin/python"

  val mockCmdResults = Map(
    ldversionCmd  -> "3.9",
    libPathCmd    -> Seq(libpl, base_prefix).mkString(";"),
    executableCmd -> executable
  )

  def callProcess(cmd: Seq[String]): Try[String] = Success {
    val cmd0 = cmd.mkString(" ")
    mockCmdResults
      .find { case (k, _) => cmd0.contains(k) }
      .map { case (_, v) => v }
      .getOrElse("")
  }

  val fs = Jimfs.newFileSystem(Configuration.unix)

  val presetProperties =
    Seq("jna.library.path", "scalapy.python.library", "scalapy.python.programname")
      .map(p => p -> Properties.propOrNone(p))
      .toMap

  after {
    presetProperties.foreach {
      case (k, None)    => Properties.clearProp(k)
      case (k, Some(v)) => Properties.setProp(k, v)
    }
  }

  "Python" should "produce correct properties" in {
    Seq("jna.library.path", "scalapy.python.library", "scalapy.python.programname")
      .foreach(Properties.clearProp(_))

    val python = new Python(
      interpreter = Some("python"),
      callProcess = callProcess,
      getEnv = Map.empty.get,
      fs = fs,
      isWindows = Some(false)
    )

    val expectedLDLibrary = s"python$ldversion"
    val expectedLibPaths  = Seq(libpl, base_prefix)
    val expectedExe       = executable
    val expectedProps = Map(
      "jna.library.path"           -> expectedLibPaths.mkString(":"),
      "scalapy.python.library"     -> expectedLDLibrary,
      "scalapy.python.programname" -> expectedExe
    )

    python.nativeLibrary.get should equal(expectedLDLibrary)
    python.nativeLibraryPaths.get should equal(expectedLibPaths)
    python.executable.get should equal(expectedExe)
    python.scalapyProperties.get should equal(expectedProps)
  }

  it should "be idempotent" in {
    val expectedLDLibrary = s"python$ldversion"
    val expectedLibPaths  = Seq(libpl, base_prefix)
    val expectedExe       = executable
    val expectedProps = Map(
      "jna.library.path"           -> expectedLibPaths.mkString(":"),
      "scalapy.python.library"     -> expectedLDLibrary,
      "scalapy.python.programname" -> expectedExe
    )

    expectedProps.foreach { case (k, v) => Properties.setProp(k, v) }

    val python = new Python(
      interpreter = Some("python"),
      callProcess = callProcess,
      getEnv = Map.empty.get,
      fs = fs,
      isWindows = Some(false)
    )

    python.nativeLibrary.get should equal(expectedLDLibrary)
    python.nativeLibraryPaths.get should equal(expectedLibPaths)
    python.scalapyProperties.get should equal(expectedProps)
  }
}
