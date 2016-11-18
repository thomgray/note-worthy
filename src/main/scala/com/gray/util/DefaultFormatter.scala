package com.gray.util

trait DefaultFormatter extends Formatting {

  def formatString(string: String, width: Int) = splitLines(string).map(wrapSingleLine(_, width)).mkString("\n")

  def replaceTabsWithSpaces(string: String) = {
    string.replaceAll("\\t", "    ")
  }

  def wrapLines(string: String, width: Int, wrapPrefix: String = "", wrapSuffix: String = "") = {
    splitLines(string).map(wrapSingleLine(_, width, wrapPrefix, wrapSuffix)).mkString("\n")
  }

  def wrapSingleLine(singleLine: String, width: Int): String = {
    val wrapRegex = s"""^.{0,${width - 1}}(\\s|_|-)""".r
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

  //TODO implement suffix
  def wrapSingleLine(singleLine: String, width: Int, wrapPrefix: String, wrapSuffix: String): String = {
    val whiteSpaceInFrontOfFirstLine = "^\\s*".r.findFirstIn(singleLine).getOrElse("")
    var outLine = wrapRegex(width).findFirstIn(singleLine).getOrElse(singleLine.substring(0, Math.min(width, singleLine.length))).stripSuffix(" ")
    var remainingInLine = singleLine.stripPrefix(outLine).trim
    if (remainingInLine.length > 0) outLine += wrapSuffix
    val wrapWidth = width - whiteSpaceInFrontOfFirstLine.length - wrapPrefix.length
    val wrapReg = wrapRegex(wrapWidth)

    while (remainingInLine.length > 0) {
      val nextLine = wrapReg.findFirstIn(remainingInLine).getOrElse(remainingInLine.substring(0, Math.min(wrapWidth, remainingInLine.length))).trim
      outLine += "\n" + whiteSpaceInFrontOfFirstLine + wrapPrefix + nextLine + wrapSuffix
      remainingInLine = remainingInLine.stripPrefix(nextLine).trim
    }
    outLine
  }

  private def wrapRegex(width: Int) = s"""^.{0,${width - 1}}(\\s|_|-|$$)""".r

  def padAndAlignBlock(string: String, lPad: String = "", rPad: String = "", align: String = "left") = {
    val lines = string.split("\n")
    val maxWidth = lines.map(trueLength).foldRight(0)(Math.max)

    lines.map(line => align match {
      case "left" =>
        val extraPad = concatenate(" ", maxWidth - trueLength(line))
        lPad + line + extraPad + rPad
      case "right" =>
        val extraPad = concatenate(" ", maxWidth - trueLength(line))
        lPad + extraPad + line + rPad
      case "center" =>
        val lExtraLength = maxWidth - trueLength(line) / 2
        val rExtraLength = maxWidth - trueLength(line) - lExtraLength
        val lExtra = concatenate(" ", lExtraLength)
        val rExtra = concatenate(" ", rExtraLength)
        lPad + lExtra + string + rExtra + rPad
      case _ => throw new UnsupportedOperationException(s"Alignment must be either 'right', 'left' or 'center'. You specified $align")
    }).mkString("\n")
  }

  def padSingleLine(string: String, width: Int, align: String = "left") = align match {
    case "left" =>
      val rpad = width - trueLength(string)
      string + concatenate(" ", rpad)
    case "right" =>
      val lpad = width - trueLength(string)
      concatenate(" ", lpad) + string
    case "center" =>
      val lpad = width - trueLength(string) / 2
      val rpad = width - trueLength(string) - lpad
      concatenate(" ", lpad) + string + concatenate(" ", rpad)
    case _ => throw new UnsupportedOperationException(s"Alignment must be either 'right', 'left' or 'center'. You specified $align")
  }

  def stitchString(strings: List[String]) = {
    val stringLines = strings.map(_.split("\n").toList)
    val longestLines = stringLines.map(_.length).foldRight(0)(Math.max)
    val paddedLines = stringLines.map { lines =>
      val length = trueLength(lines.headOption.getOrElse(""))
      lines.padTo(longestLines, concatenate(" ", length))
    }
    ((0 until longestLines) map {i =>
      paddedLines.map(l => l(i)).mkString
    }).mkString("\n")
  }

}

object DefaultFormatter extends DefaultFormatter
