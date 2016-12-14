package com.gray.markdown.parsing

import com.gray.markdown.{MdLinkRef, MdParagraph}
import com.gray.util.Ranj

import scala.collection.mutable
import scala.util.matching.Regex

protected[parsing] abstract class MdParsingRuleBase extends MdRegexes {

  protected[parsing] val stringBuffer = new mutable.MutableList[String]()

  protected[parsing] val docString: String

  protected[parsing] lazy val lines: List[String] = initLines(docString)

  protected[parsing] var linkRefs = List.empty[MdLinkRef]

  protected def initLines(string: String) = {
    val lns = string.split("\n")
    val linesBuffer = new mutable.MutableList[String]()
    lns.foreach { l =>
      MdLinkRefRegex.findFirstMatchIn(l) match {
        case None => linesBuffer += l
        case Some(mtch) => linkRefs = linkRefs :+ MdLinkRef(mtch.group(2), mtch.group(1))
      }
    }
    linesBuffer.toList
  }

  val paragraphs: mutable.MutableList[MdParagraph] = new mutable.MutableList[MdParagraph]()

  protected[parsing] var marker = 0

  protected[parsing] def getLine(int: Int = marker) = lines(int)

  protected[parsing] def getLine = lines(marker)

  protected def getLines(range: Ranj) = lines.slice(range.start, range.end)

  protected def getLines(from: Int, until: Int) = lines.slice(from, until)

  protected[parsing] def addToBuffer(par: MdParagraph) = paragraphs += par

  def lineMatchesRegex(regex: Regex, ln: Int = marker) = regex.findFirstIn(lines(ln)).isDefined

  def lineMatchesRegex(regex: Regex, ln: String) = regex.findFirstIn(ln).isDefined

  def lineMatchesAnyRegex(ln: String, regex: Regex*) = regex.exists(_.findFirstIn(ln).isDefined)

  def lineMatchesAllRegex(ln: String, regex: Regex*) = !regex.exists(_.findFirstIn(ln).isEmpty)

  def indexOfNextMatch(regex: Regex, from: Int = 0) = lines.indexWhere(ln => lineMatchesRegex(regex, ln), from) match {
    case -1 => None
    case other => Some(other)
  }

  /**
    * Returns an optional range where the start matches the current or specified line and the end is the next line that matches the second regex plus one. getLines(range) will therefore return the lines within that range including the end match<br/>
    * The start of the range (if defined) will always be equal to the from parameter, equal to the current line if unset
    *
    * @param regex1 the regex matching the beginning of the range
    * @param regex2 the regex matching the end of the range
    * @param from   optional specication of the start of the search
    * @return the range starting from the 'from' parameter of 'marker' if unspecified, ending after next match of the second regex, so the line matching the second regex equals lines(end-1), and the whole span is captured by getLines(range)
    */
  def getRangeOfSpanRegex(regex1: Regex, regex2: Regex, from: Int = marker) = lineMatchesRegex(regex1, from) match {
    case true =>
      lines.indexWhere(lineMatchesRegex(regex2, _), from + 1) match {
        case -1 => None
        case end => Some(Ranj(from, end + 1))
      }
    case false => None
  }

  /**
    * Returns an optional range of indeterminate length so long as the starting line matches the regex and any number of consecutive lines.
    *
    * @param regex the regex to match
    * @param from  the begin of the match, 'marker' if unset
    * @return the range from the start of the match to the end of the match. If the rest of the lines buffer matches the regex, then the end will be equal to lines.length, and getLines(range) will return the lines matching the regex (provided the first line matches)
    */
  def getRangeOfSolidBlock(regex: Regex, from: Int = marker) = {
    if (from < lines.length) regex.findFirstIn(lines(from)) match {
      case Some(_) =>
        lines.indexWhere(l => regex.findFirstIn(l).isEmpty, from + 1) match {
          case -1 => Some(Ranj(from, lines.length))
          case other => Some(Ranj(from, other))
        }
      case None => None
    }
    else None
  }

}
