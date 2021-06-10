```scala

val python = Python()

python.library
// res0: util.Try[String] = Success(value = "python3.9")

python.nativeLibPaths
// res1: util.Try[Seq[String]] = Success(value = List("/Users/kien/.pyenv/versions/3.9.0"))

python.scalapyProps
// res2: Map[String, String] = Map(
//   "jna.library.path" -> "/Users/kien/.pyenv/versions/3.9.0/lib:/Users/kien/.pyenv/versions/3.9.0/lib",
//   "scalapy.python.library" -> "python3.9"
// )
```

```scala
val python = Python("/usr/local/bin/python3")

python.library
// res0: util.Try[String] = Success(value = "python3.9")
python.nativeLibPaths
// res1: util.Try[Seq[String]] = Success(value = List("/usr/local/opt/python@3.9/Frameworks/Python.framework/Versions/3.9/lib"))
```
