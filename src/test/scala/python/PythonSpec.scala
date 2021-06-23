package ai.kien.python

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{BeforeAndAfter, PrivateMethodTester}
import com.google.common.jimfs.{Configuration, Jimfs}
import scala.util.{Properties, Success, Try}

class PythonSpec extends AnyFlatSpec with Matchers with PrivateMethodTester with BeforeAndAfter {
  val ldversionCmd = Python invokePrivate PrivateMethod[String](Symbol("ldversionCmd"))()
  val libPathCmd   = Python invokePrivate PrivateMethod[String](Symbol("libPathCmd"))()

  val ldversion   = "3.9"
  val prefix      = "/usr/local/lib"
  val exec_prefix = "/home/user/.share/local/lib"
  val libpl       = s"$exec_prefix/python$ldversion/config-$ldversion"

  val mockCmdResults = Map(
    ldversionCmd -> "3.9",
    libPathCmd   -> Seq(libpl, exec_prefix, prefix).mkString(";")
  )

  def callProcess(env: Seq[(String, String)])(cmd: Seq[String]): Try[String] = Success {
    val cmd0 = cmd.mkString(" ")
    mockCmdResults
      .find { case (k, _) => cmd0.contains(k) }
      .map { case (_, v) => v }
      .getOrElse("")
  }

  val fs = Jimfs.newFileSystem(Configuration.unix)

  val presetProperties = Seq("jna.library.path", "scalapy.python.library")
    .map(p => p -> Properties.propOrNone(p))
    .toMap

  after {
    presetProperties.foreach {
      case (k, None)    => Properties.clearProp(k)
      case (k, Some(v)) => Properties.setProp(k, v)
    }
  }

  "Python" should "produce correct properties" in {
    Seq("jna.library.path", "scalapy.python.library").foreach(Properties.clearProp(_))

    val python = new Python(
      interpreter = Some("python"),
      callProcess = callProcess,
      env = Map.empty,
      fs = fs,
      isWindows = Some(false)
    )

    val expectedLDLibrary = s"python$ldversion"
    val expectedLibPaths  = Seq(libpl, exec_prefix, prefix)
    val expectedProps = Map(
      "jna.library.path"       -> expectedLibPaths.mkString(":"),
      "scalapy.python.library" -> expectedLDLibrary
    )

    python.nativeLibrary.get should equal(expectedLDLibrary)
    python.nativeLibraryPaths.get should equal(expectedLibPaths)
    python.scalapyProperties.get should equal(expectedProps)
  }

  it should "be idempotent" in {
    val expectedLDLibrary = s"python$ldversion"
    val expectedLibPaths  = Seq(libpl, exec_prefix, prefix)
    val expectedProps = Map(
      "jna.library.path"       -> expectedLibPaths.mkString(":"),
      "scalapy.python.library" -> expectedLDLibrary
    )

    expectedProps.foreach { case (k, v) => Properties.setProp(k, v) }

    val python = new Python(
      interpreter = Some("python"),
      callProcess = callProcess,
      env = Map.empty,
      fs = fs,
      isWindows = Some(false)
    )

    python.nativeLibrary.get should equal(expectedLDLibrary)
    python.nativeLibraryPaths.get should equal(expectedLibPaths)
    python.scalapyProperties.get should equal(expectedProps)
  }
}
