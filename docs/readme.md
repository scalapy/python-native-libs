## Overview

The canonical use case is to help set up [`ScalaPy`](https://scalapy.dev/) to point to a specific Python installation by attempting to infer the correct configuration properties used by `ScalaPy` during the initialization of the embedded Python interpreter. This could potentially see usage outside of `ScalaPy` too since these properties are relevant to embedded Python in general.

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

python.ldflags
```

You can point it towards a specific Python installation by passing the path to the interpreter executable to `Python`

```scala mdoc:nest
val python = Python("@PYTHON@")

python.nativeLibrary

python.nativeLibraryPaths

python.scalapyProperties

python.ldflags
```

See `docs/details.md` to see the full list of these properties and what they mean.

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
