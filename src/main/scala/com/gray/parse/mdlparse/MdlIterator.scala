package com.gray.parse.mdlparse

import com.gray.parse.{Location, ParseIterator, ParseResult}
import com.gray.util.{Formatting, Ranj}

class MdlIterator(string: String, offset: Int = 0) extends ParseIterator(offset) with MdlParseConstants with Formatting {

  val lines = trimEmptyLines(string).split("(\\n|\\r)")

  def end = lines.length

  private var marker = 0

  val blankRegex = "^\\s*$".r

  override def nextThing: Option[ParseResult] = {
    getRangeOfNextBlock(marker) match {
      case range@Some(Ranj(start, end)) if marker < start && !linesAreBlank(marker, start) =>
        val array = lines.slice(marker, start)
        marker = start
        Some(handleStringLine(array, range.get))
      case range@Some(Ranj(start, end)) =>
        val array = lines.slice(start, end)
        marker = end
        Some(handleTagLines(array, range.get))
      case None if marker < end && !linesAreBlank(marker, end) =>
        val array = lines.slice(marker, lines.length)
        marker = end
        Some(handleStringLine(array, Ranj(marker, lines.length)))
      case _ => None
    }
  }

  private def handleTagLines(lines: Array[String], ranj: Ranj) = {
    val emptySpace = lines.head.substring(0, lines.head.indexOf('['))
    val _lines = lines.map { l =>
      if (l.startsWith(emptySpace)) l.stripPrefix(emptySpace)
      else l.stripPrefix(blankRegex.findFirstIn(l).getOrElse(""))
    }
    val firstLine = _lines.head

    val chevronPrefix = s"^\\[{3,}(\\$PARENT_VISIBLE_FLAG|\\$UNIVERSAL_REFERENCE_FLAG|\\$CONTENT_INVISIBLE_FLAG| )*".r.findFirstIn(firstLine).get

    val labels = firstLine.stripPrefix(chevronPrefix).split(";").map(_.trim).toList
    val string = _lines.slice(1, _lines.length-1).mkString("\n")

    var argString = ""
    if (chevronPrefix.contains(PARENT_VISIBLE_FLAG)) argString += PARENT_VISIBLE_FLAG
    if (chevronPrefix.contains(UNIVERSAL_REFERENCE_FLAG)) argString += UNIVERSAL_REFERENCE_FLAG
    if (chevronPrefix.contains(CONTENT_INVISIBLE_FLAG)) argString += CONTENT_INVISIBLE_FLAG

    val colStart = "\\[{3,}".r.findFirstMatchIn(lines.head).get.end
    val colEnd = "\\]{3,}".r.findFirstMatchIn(lines.last).get.start

    val location = Location(ranj.start + offset, ranj.end + offset, colStart, colEnd)

    ParseResult(string, Some(labels), CONTENT_TAG, argString, location)
  }

  private def handleStringLine(lines: Array[String], ranj: Ranj) = {
    val string1 = trimEmptyLines(lines.mkString("\n"))
    val sensibleLines = string1.split("\n")

    val colStart = "\\S".r.findFirstMatchIn(sensibleLines.head).get.end + 1
    val colEnd = "\\S*$".r.findFirstMatchIn(sensibleLines.last).get.start + 1
    val location = Location(ranj.start + offset, ranj.end + offset, colStart, colEnd)

    ParseResult(string1, None, CONTENT_STRING, "", location)
  }
  
  private def handleLink(line: String, ranj: Ranj) = {
    
  }


  def getRangeOfNextBlock(from: Int): Option[Ranj] = {
    getLineNumberOfNextOpenLine(from) match {
      case Some(i) =>
        var lr = 1
        for (j <- i + 1 until lines.length) {
          val nextLine = lines(j).trim
          if (nextLine.startsWith(openLinePrefix)) lr += 1
          else if (nextLine.startsWith(closeLinePrefix)) lr -= 1
          if (lr == 0) return Some(Ranj(i, j + 1))
        }
        None
      case None => None
    }
  }

  def getLineNumberOfNextOpenLine(from: Int): Option[Int] =
    lines.indexWhere(_.trim.startsWith(openLinePrefix), from) match {
      case -1 => None
      case other => Some(other)
    }

  def getLineNumberOfNextCloseLine(from: Int): Option[Int] = {
    if (from < lines.length) {
      for (i <- from until lines.length) {
        val line = lines(i).trim
        if (line.startsWith(closeLinePrefix)) return Some(i)
      }
      None
    } else None
  }

  private def linesAreBlank(start: Int, end: Int) = {
    (for (i <- start until end if blankRegex.findFirstIn(lines(i)).isEmpty)
      yield true).isEmpty
  }
}


object MdlIterator {
  def apply(string: String, linesOffset: Int = 0): MdlIterator = new MdlIterator(string, linesOffset)
}
