## Overview

The canonical use case is to help set up [`ScalaPy`](https://scalapy.dev/) to point to a specific Python installation by attempting to calculate the correct configuration parameters used by `ScalaPy` during the initialization of the embedded Python interpreter. Could be useful for embedded Python setup in general, not just `ScalaPy`.

## Installation

```scala
libraryDependencies += "ai.kien" %% "python-native-libs" % "@VERSION@"
```

## Usage

By default `Python` checks for the `python3` executable (or `python` if `python3` is not found) on `PATH`

```scala mdoc
import ai.kien.python.Python

val python = Python()

python.nativeLibrary

python.nativeLibraryPaths

python.scalapyProperties
```

You can point it towards a specific Python installation by passing the path to the interpreter executable to `Python`

```scala mdoc:nest
val python = Python("@PYTHON@")

python.nativeLibrary

python.nativeLibraryPaths

python.scalapyProperties
```

`scalapyProperties` contains the system properties used by `ScalaPy`. For example, to set up `ScalaPy` to use the Python located at `@PYTHON@` in [`Ammonite`](https://ammonite.io/) or [`Almond`](https://almond.sh/) run

```scala
import $ivy.`ai.kien::python-native-libs:@VERSION@`
import ai.kien.python.Python

Python("@PYTHON@").scalapyProperties.fold(
  ex => println(s"Error while getting ScalaPy properties: $ex"),
  props => props.foreach { case(k, v) => System.setProperty(k, v) }
)


import $ivy.`me.shadaj::scalapy-core:@SCALAPY_VERSION@`
import me.shadaj.scalapy.py

println(py.module("sys").version)
```
