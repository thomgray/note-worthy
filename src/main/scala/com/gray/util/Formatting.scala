package com.gray.util

import com.gray.note.Config

import scala.io.AnsiColor

trait Formatting extends AnsiColor{

  def trimEmptyLines(string: String) = {
    val prefix = """^(\s*(\n|\r))*""".r.findFirstIn(string).getOrElse("")
    val suffix = """\s*$""".r.findFirstIn(string).getOrElse("")
    string.stripPrefix(prefix).stripSuffix(suffix)
  }

  def indentString(string: String, indent: String = Config.standardTab) = {
    string.split("(\\n|\\r)").map(indent + _).mkString("\n")
  }

  def concatenate(string: String, times: Int) = (for (_ <- 0 until times) yield string).mkString

}
