#!/bin/sh

VENV_DIR=$(mktemp -d)

echo "Virtualenv created at ${VENV_DIR}"

python3 -m venv "${VENV_DIR}"
"${VENV_DIR}"/bin/python -m pip install examples/python-package

sbt +publishLocal

sbt \
  -Dplugin.virtualenv=true \
  -Dplugin.python.executable="${VENV_DIR}/bin/python" \
  -Dplugin.scalapy.version="$1" \
  tests/scripted

echo "Delete virtualenv ${VENV_DIR}"
rm -r "${VENV_DIR}"
