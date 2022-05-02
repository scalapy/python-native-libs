#!/bin/sh

sbt +publishLocal
sbt -Dplugin.ci=true -Dplugin.python.executable="$1" -Dplugin.scalapy.version="$2" scripted
