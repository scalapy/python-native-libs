#!/bin/bash

sbt +publishLocal

version=$(sbt -Dsbt.supershell=false -error "print version")

test() {
  cd .github/project
  sbt +rootJVM/run +rootNative/run
}

CI_LIBRARY_VERSION="$version" CI_PYTHONEXECUTABLE="$1" CI_SCALAPY_VERSION="$2" test
