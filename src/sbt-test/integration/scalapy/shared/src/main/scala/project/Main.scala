package project

import me.shadaj.scalapy.py
import me.shadaj.scalapy.py.{PyQuote, SeqConverters}

object Main {
  def main(args: Array[String]): Unit = {
    println(py.Dynamic.global.list(Seq(1, 2, 3).toPythonProxy))
    println(py"'Hello from ScalaPy!'")
  }
}
