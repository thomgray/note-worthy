package com.gray.parse.mdlparse

import com.gray.parse.{ParseIterator, ParseResult, Range}
import com.gray.util.Formatting

class MdlIterator(string: String) extends ParseIterator with MdlParseConstants with Formatting {
  val lines = trimEmptyLines(string).split("(\\n|\\r)")
  def end = lines.length
  private var marker = 0

  val blankRegex = "^\\s*$".r

  override def  nextThing: Option[ParseResult] = {
    getRangeOfNextBlock(marker) match {
      case Some(Range(start, end)) if marker < start && !linesAreBlank(marker, start) =>
        val array = getLinesInRange(marker, start)
        marker = start
        Some(handleStringLine(array))
      case Some(Range(start, end)) =>
        val array = getLinesInRange(start, end)
        marker = end
        Some(handleTagLines(array))
      case None if marker < end =>
        val array = getLinesInRange(marker, lines.length)
        marker = end
        Some(handleStringLine(array))
      case _ => None
    }
  }

  private def handleTagLines(lines: Array[String]) = {
    val emptySpace = lines.head.substring(0, lines.head.indexOf('['))
    val _lines = lines.map {l =>
      if (l.startsWith(emptySpace)) l.stripPrefix(emptySpace)
      else l.stripPrefix(blankRegex.findFirstIn(l).getOrElse(""))
    }
    val firstLine = _lines.head

    val chevronPrefix = "^\\[{3,}(\\*|\\^)*".r.findFirstIn(firstLine).get

    val labels = firstLine.stripPrefix(chevronPrefix).split(";").map(s=>s.trim).toList
    val string = getLinesInRange(1, _lines.length-1, _lines).mkString("\n")

    var argString = ""
    if (chevronPrefix.contains(PARENT_VISIBLE_FLAG)) argString += PARENT_VISIBLE_FLAG
    if (chevronPrefix.contains(UNIVERSAL_REFERENCE_FLAG)) argString += UNIVERSAL_REFERENCE_FLAG

    ParseResult(string, Some(labels), CONTENT_TAG, argString)
  }

  private def handleStringLine(lines: Array[String]) = {
    val string0 = lines.mkString("\n")
    val string1 = trimEmptyLines(string0)
    ParseResult(string1, None, CONTENT_STRING, "")
  }

  def getRangeOfNextBlock(from: Int): Option[Range] = {
    getLineNumberOfNextOpenLine(from) match {
      case Some(i) =>
        var lr = 1
        for (j <- i+1 until lines.length){
          val nextLine = lines(j).trim
          if (nextLine.startsWith(openLinePrefix)) lr += 1
          else if (nextLine.startsWith(closeLinePrefix)) lr -= 1
          if (lr==0) return Some(Range(i, (j+1)))
        }
        None
      case None => None
    }
  }

  def getLineNumberOfNextOpenLine(from: Int): Option[Int] = {
    if (from < lines.length){
      for (i <- from until lines.length){
        val line = lines(i).trim
        if (line.startsWith(openLinePrefix)) return Some(i)
      }
      None
    } else None
  }
  def getLineNumberOfNextCloseLine(from: Int): Option[Int] = {
    if (from < lines.length){
      for (i <- from until lines.length){
        val line = lines(i).trim
        if (line.startsWith(closeLinePrefix)) return Some(i)
      }
      None
    } else None
  }

  private def getLinesInRange(start: Int, end: Int, array: Array[String] = lines) = {
    (for (i <- start until end) yield array(i)).toArray
  }

  private def linesAreBlank(start: Int, end : Int) = {
    (for (i <- start until end if blankRegex.findFirstIn(lines(i)).isEmpty)
      yield true).isEmpty
  }
}


object MdlIterator {
  def apply(string: String): MdlIterator = new MdlIterator(string)
}
