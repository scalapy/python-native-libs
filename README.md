## Overview

The canonical use case is to help set up [`ScalaPy`](https://scalapy.dev/) to point to a specific Python installation by attempting to infer the correct configuration properties used by `ScalaPy` during the initialization of the embedded Python interpreter. This could potentially see usage outside of `ScalaPy` too since these properties are relevant to embedded Python in general.

## Installation

```scala
libraryDependencies += "ai.kien" %% "python-native-libs" % "0.1.2"
```

## Usage

By default `Python` checks for the `python3` executable (or `python` if `python3` is not found) on `PATH`

```scala
import ai.kien.python.Python

val python = Python()
// python: Python = ai.kien.python.Python@59f72d34

python.nativeLibrary
// res0: util.Try[String] = Success(value = "python3.7m")

python.nativeLibraryPaths
// res1: util.Try[Seq[String]] = Success(
//   value = ArraySeq(
//     "/home/kien/.pyenv/versions/3.7.2/lib/python3.7/config-3.7m-x86_64-linux-gnu",
//     "/home/kien/.pyenv/versions/3.7.2/lib"
//   )
// )

python.scalapyProperties
// res2: util.Try[Map[String, String]] = Success(
//   value = Map(
//     "jna.library.path" -> "/home/kien/.pyenv/versions/3.7.2/lib/python3.7/config-3.7m-x86_64-linux-gnu:/home/kien/.pyenv/versions/3.7.2/lib",
//     "scalapy.python.library" -> "python3.7m",
//     "scalapy.python.programname" -> "/home/kien/.pyenv/versions/3.7.2/bin/python3"
//   )
// )

python.ldflags
// res3: util.Try[Seq[String]] = Success(
//   value = ArraySeq(
//     "-L/home/kien/.pyenv/versions/3.7.2/lib/python3.7/config-3.7m-x86_64-linux-gnu",
//     "-L/home/kien/.pyenv/versions/3.7.2/lib",
//     "-lpython3.7m",
//     "-lpthread",
//     "-ldl",
//     "-lutil",
//     "-lm",
//     "-Xlinker",
//     "-export-dynamic"
//   )
// )
```

You can point it towards a specific Python installation by passing the path to the interpreter executable to `Python`

```scala
val python = Python("/usr/bin/python3")
// python: Python = ai.kien.python.Python@4fded021

python.nativeLibrary
// res4: util.Try[String] = Success(value = "python3.8")

python.nativeLibraryPaths
// res5: util.Try[Seq[String]] = Success(
//   value = ArraySeq("/usr/lib/python3.8/config-3.8-x86_64-linux-gnu", "/usr/lib")
// )

python.scalapyProperties
// res6: util.Try[Map[String, String]] = Success(
//   value = Map(
//     "jna.library.path" -> "/usr/lib/python3.8/config-3.8-x86_64-linux-gnu:/usr/lib",
//     "scalapy.python.library" -> "python3.8",
//     "scalapy.python.programname" -> "/usr/bin/python3"
//   )
// )

python.ldflags
// res7: util.Try[Seq[String]] = Success(
//   value = ArraySeq(
//     "-L/usr/lib/python3.8/config-3.8-x86_64-linux-gnu",
//     "-L/usr/lib",
//     "-lpython3.8",
//     "-lcrypt",
//     "-lpthread",
//     "-ldl",
//     "-lutil",
//     "-lm",
//     "-lm"
//   )
// )
```

See `docs/details.md` to see the full list of these properties and what they mean.

`scalapyProperties` contains the system properties used by `ScalaPy`. For example, to set up `ScalaPy` to use the Python located at `/usr/bin/python3` in [`Ammonite`](https://ammonite.io/) or [`Almond`](https://almond.sh/) run

```scala
import $ivy.`ai.kien::python-native-libs:0.1.2`
import ai.kien.python.Python

Python("/usr/bin/python3").scalapyProperties.fold(
  ex => println(s"Error while getting ScalaPy properties: $ex"),
  props => props.foreach { case(k, v) => System.setProperty(k, v) }
)


import $ivy.`me.shadaj::scalapy-core:0.5.0`
import me.shadaj.scalapy.py

println(py.module("sys").version)
```
