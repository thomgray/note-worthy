package com.gray.util

trait DefaultFormatter extends Formatting{

  def formatString(string: String, width: Int) = {
    val list = for {
      line <- splitLines(string)
      wrapped = wrapSingleLine(line, width)
    } yield wrapped

    list.mkString("\n")
  }

  def replaceTabsWithSpaces(string: String) = {
    string.replaceAll("\\t", "    ")
  }

  def wrapLines(string: String, width: Int) = {
    string.split("(\\n|\\r)").map(wrapSingleLine(_, width)).mkString("\n")
  }

  private def wrapSingleLine(singleLine: String, width: Int): String = {
    val wrapRegex = s"""^.{0,${width-1}}(\\s|_|-)""".r
    val whitespacePrefix = "^\\s*".r.findFirstIn(singleLine).getOrElse("")
    var outLine = ""
    var inline = singleLine
    while (inline.length > width) {
      val pref = wrapRegex.findFirstIn(inline).getOrElse(inline.substring(0, width)).stripSuffix(" ")
      outLine += pref + "\n"
      inline = whitespacePrefix + inline.stripPrefix(pref).trim
    }
    outLine + inline
  }

  def splitLines(string: String) = string.split("(\\r|\\n)")
}

object DefaultFormatter extends DefaultFormatter
