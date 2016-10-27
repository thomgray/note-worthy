package com.gray.parse.mdlparse

import com.gray.parse.{ParseIterator, ParseResult, Range}

class MdlIterator(string: String) extends ParseIterator with MdlParseConstants {
  val lines = getLines
  def end = lines.length
  private var marker = 0

  val blankRegex = "^\\s*$".r

  private def getLines = {
    val prefix = """^(\s*(\n|\r))*""".r.findFirstIn(string).getOrElse("")
    val suffix = """\s*$""".r.findFirstIn(string).getOrElse("")
    string.stripPrefix(prefix).stripSuffix(suffix).split("(\\n|\\r)")
  }

  override def  nextThing: Option[ParseResult] = {
    getRangeOfNextBlock(marker) match {
      case None if marker < end =>
        val array = getLinesInRange(marker, lines.length)
        marker = end
        Some(handleStringLine(array))
      case Some(Range(start, end)) if marker < start && linesAreBlank(marker, start) =>
        val array = getLinesInRange(marker, start)
        marker = start
        Some(handleStringLine(array))
      case Some(Range(start, end)) =>
        val array = getLinesInRange(start, end)
        marker = end
        Some(handleTagLines(array))
      case _ => None
    }
  }

  private def handleTagLines(lines: Array[String]) = {
    val firstLine = lines.head
    val emptySpace = firstLine.substring(0, firstLine.indexOf('>'))
    val _lines = lines.map(l=>l.stripPrefix(emptySpace))

    val chevronPrefix = s"^$emptySpace>>>>*(\\*|\\^)*".r.findFirstIn(lines(0)).get

    val labels = firstLine.stripPrefix(chevronPrefix).split(";").map(s=>s.trim).toList
    val string = getLinesInRange(1, _lines.length-1, _lines).mkString("\n")

    ParseResult(string, Some(labels), CONTENT_TAG, List())
  }

  private def handleStringLine(lines: Array[String]) = {
    val string = lines.mkString("\n")
    ParseResult(string, None, CONTENT_STRING, List())
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
//    val out = new Array[String](end - start)
//    Array.copy(array, start, out, 0, out.length)
//    out
    (for (i <- start until end) yield array(i)).toArray
  }

  private def linesAreBlank(start: Int, end : Int) = {
    (for (i <- start until end if blankRegex.findFirstIn(lines(i)).isDefined)
      yield true).isEmpty
  }
}


object MdlIterator {
  def apply(string: String): MdlIterator = new MdlIterator(string)
}
