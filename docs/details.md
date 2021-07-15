# Details

## `scalapy.python.library`/`nativeLibrary`

Name of the Python shared library, *i.e* the Python shared library file name (*e.g* `libpython3.9m.dylib`, `libpython3.7.so`) without the `lib` prefix and the file extension.

The Python shared library take the form of `python{ldversion}` where `ldversion` is `{major}.{minor}{abiflags}`. There are 2 ways to get the name of the Python shared library

```python
from sysconfig import get_config_var

ldversion = get_config_var("LDVERSION")

python_native_lib = f"python{ldversion}"
```

or

```python
from sys import abiflags
import sysconfig

version = sysconfig.get_python_version()

python_native_lib = f"python{version}{abiflags}"
```

We went with the 2nd method since it is more portable.

## `jna.library.path`/`nativeLibraryPaths`

The directory where the Python shared library lives. It is located in either `sys.base_prefix` or config dir `sysconfig.get_config_var('LIBPL')`. For each Python installation, which one of these 2 directories actually contains the shared library is almost arbitrary. We decided to just use both with priority given to the config dir `f"{sysconfig.get_config_var('LIBPL')}:{sys.base_prefix}"`.

## `scalapy.python.programname`/`executable`

The path to the Python interpreter executable, used as the input to `Py_SetProgramName`. The Python doc recommends running `Py_SetProgramName` prior to `Py_Initialize` so that the correct Python run-time libraries relative to the interpreter executable can be found (`prefix`, `exec_prefix`, ...)

https://docs.python.org/3/extending/embedding.html#very-high-level-embedding
https://docs.python.org/3/c-api/init.html#c.Py_SetProgramName

This enables using ScalaPy with virtualenv. Since a Python installation inside virtualenv shares the same shared library (`libpython...so`) with the base installation, without `Py_SetProgramName`, there's no way to point ScalaPy towards the runtime directories set by virtualenv.

```python
import sys

sys.executable
```
