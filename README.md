## Installation

```scala
libraryDependencies += "ai.kien" %% "python-native-libs" % "0.1.0-SNAPSHOT"
```

## Usage

```scala
import ai.kien.python.Python

val python = Python()
// python: Python = ai.kien.python.Python@d2e2016

python.nativeLibrary
// res0: util.Try[String] = Success(value = "python3.9")

python.nativeLibraryPaths
// res1: util.Try[Seq[String]] = Success(
//   value = List("/Users/kien/.pyenv/versions/3.9.0/lib")
// )

python.scalaPyProperties
// res2: util.Try[Map[String, String]] = Success(
//   value = Map(
//     "jna.library.path" -> "/Users/kien/.pyenv/versions/3.9.0/lib",
//     "scalapy.python.library" -> "python3.9"
//   )
// )
```

```scala
val python = Python("/usr/local/bin/python3")
// python: Python = ai.kien.python.Python@631aeed3

python.nativeLibrary
// res3: util.Try[String] = Success(value = "python3.9")

python.nativeLibraryPaths
// res4: util.Try[Seq[String]] = Success(
//   value = List(
//     "/usr/local/opt/python@3.9/Frameworks/Python.framework/Versions/3.9/lib"
//   )
// )

python.scalaPyProperties
// res5: util.Try[Map[String, String]] = Success(
//   value = Map(
//     "jna.library.path" -> "/usr/local/opt/python@3.9/Frameworks/Python.framework/Versions/3.9/lib",
//     "scalapy.python.library" -> "python3.9"
//   )
// )
```
