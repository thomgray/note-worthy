package com.gray.parse.mdlparse

import com.gray.markdown.{MdHeader, MdLocation, MdString}
import com.gray.note.content_things.MdPlainString
import com.gray.parse._
import com.gray.note.util.{Formatting, Ranj}

object MdlIterator extends ContentParser with MdlParseConstants with Formatting {

  val blankRegex = "^\\s*$".r

  def nextThingFrom(marker: Int, lines: Array[String], offset: Int): Option[(AbstractParseResult, Int)] = {
    val end = lines.length
    getRangeOfNextBlock(marker, lines) match {
      case range@Some(Ranj(start, end)) if marker < start && !linesAreBlank(marker, start, lines) =>
        val array = lines.slice(marker, start)
        Some(handleStringLine(array, range.get, offset), start)
      case range@Some(Ranj(start, end)) =>
        val array = lines.slice(start, end)
        Some(handleTagLines(array, range.get, offset), end)
      case None if marker < end && !linesAreBlank(marker, end, lines) =>
        val array = lines.slice(marker, lines.length)
        Some(handleStringLine(array, Ranj(marker, lines.length), offset), end)
      case _ => None
    }
  }

  private def handleTagLines(lines: Array[String], ranj: Ranj, offset: Int) = {
    val emptySpace = lines.head.substring(0, lines.head.indexOf('['))
    val _lines = lines.map { l =>
      if (l.startsWith(emptySpace)) l.stripPrefix(emptySpace)
      else l.stripPrefix(blankRegex.findFirstIn(l).getOrElse(""))
    }
    val firstLine = _lines.head

    val chevronPrefix = s"^\\[{3,}(\\$PARENT_VISIBLE_FLAG|\\$UNIVERSAL_REFERENCE_FLAG|\\$CONTENT_INVISIBLE_FLAG| )*".r.findFirstIn(firstLine).get

    val labels = firstLine.stripPrefix(chevronPrefix).split(";").map(_.trim).toList
    val innerLines = _lines.slice(1, _lines.length-1)

    var argString = ""
    if (chevronPrefix.contains(PARENT_VISIBLE_FLAG)) argString += PARENT_VISIBLE_FLAG
    if (chevronPrefix.contains(UNIVERSAL_REFERENCE_FLAG)) argString += UNIVERSAL_REFERENCE_FLAG
    if (chevronPrefix.contains(CONTENT_INVISIBLE_FLAG)) argString += CONTENT_INVISIBLE_FLAG

    val colStart = "\\[{3,}".r.findFirstMatchIn(lines.head).get.end
    val colEnd = "\\]{3,}".r.findFirstMatchIn(lines.last).get.start

    val location = MdLocation(ranj.start + offset, ranj.end + offset, colStart, colEnd)
    val headerLocation = MdLocation(ranj.start + offset, ranj.start + offset + 1)

    TagParseResult(getResultsFromLines(innerLines, ranj.start + offset), MdHeader(MdString(labels.head, headerLocation), 1, headerLocation), labels.tail)
  }

  private def handleStringLine(lines: Array[String], ranj: Ranj, offset: Int) = {
    val string1 = trimEmptyLines(lines.mkString("\n"))
    val sensibleLines = string1.split("\n")

    val colStart = "\\S".r.findFirstMatchIn(sensibleLines.head).get.end + 1
    val colEnd = "\\S*$".r.findFirstMatchIn(sensibleLines.last).get.start + 1
    val location = MdLocation(ranj.start + offset, ranj.end + offset, colStart, colEnd)

    StringParseResult(List(MdPlainString(string1, location)))
  }
  
  private def handleLink(line: String, ranj: Ranj) = {
    
  }


  def getRangeOfNextBlock(from: Int, lines: Array[String]): Option[Ranj] = {
    getLineNumberOfNextOpenLine(from, lines) match {
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

  def getLineNumberOfNextOpenLine(from: Int, lines: Array[String]): Option[Int] =
    lines.indexWhere(_.trim.startsWith(openLinePrefix), from) match {
      case -1 => None
      case other => Some(other)
    }

  def getLineNumberOfNextCloseLine(from: Int, lines: Array[String]): Option[Int] = {
    if (from < lines.length) {
      for (i <- from until lines.length) {
        val line = lines(i).trim
        if (line.startsWith(closeLinePrefix)) return Some(i)
      }
      None
    } else None
  }

  private def linesAreBlank(start: Int, end: Int, lines: Array[String]) = {
    (for (i <- start until end if blankRegex.findFirstIn(lines(i)).isEmpty)
      yield true).isEmpty
  }

  protected def getResultsFromLines(lines: Array[String], offset: Int): List[AbstractParseResult] = {
    var marker = 0
    var list = List.empty[AbstractParseResult]
    while (nextThingFrom(marker, lines, offset) match {
      case Some((res, newMarker)) =>
        list = list :+ res
        marker = newMarker
        true
      case None => false
    }){}
    list
  }

  override def apply(string: String): List[AbstractParseResult] =
    getResultsFromLines(string.split("(\\n|\\r)"), 0)

}
