## Installation

```scala
libraryDependencies += "ai.kien" %% "python-native-libs" % "@VERSION@"
```

## Usage

```scala mdoc
import ai.kien.python.Python

val python = Python()

python.nativeLibrary

python.nativeLibraryPaths

python.scalaPyProperties
```

```scala mdoc:nest
val python = Python("/usr/local/bin/python3")

python.nativeLibrary

python.nativeLibraryPaths

python.scalaPyProperties
```
