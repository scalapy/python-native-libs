package project

import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.{PyQuote, SeqConverters}

object Module {
  def main(args: Array[String]): Unit = {
    Option(System.getenv("CI_VIRTUALENV")).foreach { _ =>
      println(s"Successfully load ${py.module("dummy")}")
    }
  }
}
