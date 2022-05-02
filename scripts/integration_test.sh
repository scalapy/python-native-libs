#!/bin/sh

sbt +publishLocal
sbt -Dplugin.python.executable="$1" -Dplugin.scalapy.version="$2" tests/scripted
