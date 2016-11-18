package com.gray.util.attributed_string

import com.gray.util.Ranj

import scala.collection.mutable
import scala.io.AnsiColor
import scala.io.AnsiColor._
import scala.util.matching.Regex

class AttributedString(_string: String, _attributes: List[Attribute]) extends AbstractAttributedString[AttributedString](_string, _attributes) {

  def +(attributedString: AttributedString) = {
    val newString = string + attributedString.string
    val newAttributes = attributes ++ attributedString.attributes.map(_.translate(length))
    AttributedString.withAttributes(newString, newAttributes.toList)
  }

  def +/(attributedString: AttributedString) = this + (AttributedString("\n") + attributedString)

  override def toString: String = {
    var str = string
    for (i <- str.indices.reverse) {
      attributes.find(_.range.start == i) match {
        case Some(at) => str = str.substring(0, i) + at.formatString + str.substring(i)
        case None =>
      }
      attributes.find(_.range.end == i) match {
        case Some(at) => str = str.substring(0, i) + RESET + str.substring(i)
        case None =>
      }
    }
    str + RESET
  }

  def findFirst(regex: Regex) = regex.findFirstMatchIn(_string) match {
    case None => None
    case Some(mtch) =>
      val matchedRange = Ranj(mtch.start, mtch.end)
      val outString = _string.substring(mtch.start, mtch.end)
      val outAttributes = attributesInRange(matchedRange)
      Some(AttributedString.withAttributes(outString, outAttributes))
  }

  def padLines(width: Int) =
    AttributedString.mkStringWithLines(splitLines.map(_.padTo(width)))


  def wrapLines(width: Int, wrapPrefix: Option[AttributedString] = None, wrapSuffix: Option[AttributedString]= None) =
    AttributedString.mkStringWithLines(splitLines.map(_.wrapSingleLine(width, wrapPrefix, wrapSuffix)))


  private def wrapSingleLine(width: Int, wrapPrefix: Option[AttributedString], wrapSuffix: Option[AttributedString]) = {
    val wrapRegex1 = wrapRegex(width)
    val whiteSpaceInFrontOfFirstLine = findFirst("^\\s*".r).getOrElse(AttributedString.empty)

    var outLine = findFirst(wrapRegex1).getOrElse(substring(0, Math.min(width, length))).trimTrailing
    var remainingInLine = substring(outLine.length).trimLeading

    val wrapWidth = width - whiteSpaceInFrontOfFirstLine.length - (if (wrapPrefix.isDefined) wrapPrefix.get.length else 0) - (if (wrapSuffix.isDefined) wrapSuffix.get.length else 0)
    val wrapRegex2 = wrapRegex(wrapWidth)

    while (remainingInLine.length > 0) {
      outLine = wrapSuffix match {
        case Some(suffix) => outLine + suffix
        case _ => outLine
      }
      val nextLine = remainingInLine.findFirst(wrapRegex2).getOrElse(remainingInLine.substring(0, Math.min(wrapWidth, remainingInLine.length))).trimTrailing

      outLine = outLine +/ (wrapPrefix match {
        case Some(prefix) => whiteSpaceInFrontOfFirstLine + prefix + nextLine
        case None => whiteSpaceInFrontOfFirstLine + nextLine
      })
      remainingInLine = remainingInLine.substring(nextLine.length).trimLeading
    }
    outLine
  }

  private def wrapRegex(width: Int) = s"""^.{0,${width - 1}}(\\s|_|-|$$)""".r


  def padTo(int: Int, padString: String = " ", padAttributes:Seq[String] = Seq.empty) = if (int - length > 0) {
    val padLength = int - length
    val pad = AttributedString.padded(padLength, padString, padAttributes)
    this + pad
  } else this

  override protected[attributed_string] def newThis(str: String, atts: List[Attribute]) = new AttributedString(str, atts)

  def trimLeading = "^\\s*".r.findFirstMatchIn(string) match {
      case None => this
      case Some(mtch) =>
        substring(mtch.end)
    }


  def trimTrailing = "\\s*$".r.findFirstMatchIn(string) match {
    case None => this
    case Some(mtch) =>
      substring(0,mtch.start)
  }

  def trim = trimLeading.trimTrailing
}

object AttributedString {
  def apply(string: String, formats: Seq[String] = Seq.empty): AttributedString = {
    val att = Attribute(Ranj(0, string.length), formats)
    new AttributedString(string, List(att))
  }

  def empty = new AttributedString("", List.empty)

  def padded(width: Int, string: String = " ", attributes: Seq[String]) = {
    var str1 = string
    while (str1.length<width) str1 += string
    if (str1.length>width) str1 = str1.substring(0,width)
    AttributedString(str1, attributes)
  }

  def mkStringWithLines(lines: List[AttributedString]) =
    lines.foldLeft(AttributedString.empty)((out, line) => out +/ line).substring(1)

  def mkString(blocks: List[AttributedString]) =
    blocks.foldLeft(AttributedString.empty)((out, line) => out + line)


  def withAttributes(string: String, attributes: List[Attribute]): AttributedString = new AttributedString(string, attributes)


  val foregroundColours = List(RED, BLUE, BLACK, YELLOW, CYAN, GREEN, MAGENTA, WHITE)
  val backgroundColours = List(RED_B, BLUE_B, BLACK_B, YELLOW_B, CYAN_B, GREEN_B, MAGENTA_B, WHITE_B)
  val otherFormats = List(REVERSED, BLINK, BOLD, UNDERLINED, INVISIBLE)
}
