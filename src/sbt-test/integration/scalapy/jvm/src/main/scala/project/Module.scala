package project

import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.{PyQuote, SeqConverters}

object Module {
  def main(args: Array[String]): Unit = {
    Option(sys.props("plugin.virtualenv")).map(_.trim).filter(_.nonEmpty).foreach { _ =>
      println(s"Successfully load ${py.module("dummy")}")
    }
  }
}
