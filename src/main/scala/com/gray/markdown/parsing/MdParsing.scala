package com.gray.markdown.parsing

import com.gray.markdown._

import scala.collection.mutable
import scala.util.matching.Regex
import com.gray.markdown.Range

protected[parsing] abstract class MdParsing extends MdParsingRuleBase  with MdRegexes {

  val factory: MdFactory
  import factory._

  def parse() = {
    while (marker < lines.length) {
      checks.find(check => check()) match {
        case None => println(s"No rules for line: $getLine")
        case _ =>
      }
      marker += 1
    }
  }

  val checks = List[() => Boolean](
    checkEmptyLineBlock,
    checkHeader,
    checkCodeBlock,
    checkIndentedLiteral,
    checkList
//    checkBlockQuote
  )

  def checkHeader() = if (lineMatchesRegex(headerRegex)) {
    val header = makeMdHeader(getLine)
    addToBuffer(header)
    true
  } else false

  def checkCodeBlock() = getRangeOfSpanRegex(codeBeginRegex, codeEndRegex) match {
    case Some(range) =>
      marker = range.end - 1
      val lines = getLines(range)
      addToBuffer(makeMdCodeBlock(lines))
      true
    case None => false
  }

  def checkEmptyLineBlock() = getRangeOfSolidBlock(emptyLineRegex) match {
    case Some(range) =>
      marker = range.end - 1
      addToBuffer(MdBreak())
      true
    case None => false
  }

  def checkIndentedLiteral() = getRangeOfSolidBlock(indentedLiteralRegex) match {
    case Some(range) =>
      marker = range.end - 1
      println("FOUND INDENTED LITERAL AT: "+range)
      println("Last line = "+lines.last)
      addToBuffer(makeMdIndentedLiteral(getLines(range)))
      true
    case None => false
  }

  def checkBlockQuote() = lineMatchesRegex(blockQuoteRegex) match {
    case false => false
    case true =>
      val range = lines.indexWhere({ ln =>
        lineMatchesAnyRegex(ln, emptyLineRegex, bulletListItemRegexBegin, checkListItemRegexBegin)
      }, marker + 1) match {
        case -1 => Range(marker, lines.length)
        case other => Range(marker, other)
      }
      marker = range.end - 1
      val quote = makeMdQuoteBlock(getLines(range))
  }

  def checkList() = anyMatchingRegex(getLine, listBeginRegexes) match {
      case None => false
      case Some(regex) =>
        var previous = getLine
        val range = lines.indexWhere({ str =>
          val stop = checkListItemAfterItem(previous, str)
          previous = str
          stop
        }, marker+1) match {
          case -1 => Range(marker, lines.length)
          case other => Range(marker, other)
        }
        marker = range.end-1
        addToBuffer(makeList(getLines(range)))
        true
    }

//  private def checkListItemAfterItem(previous: String, next: String) = if (anyMatchingRegex(next, listItemRegexes).isDefined) {
//    val allowableIndent = listItemPrefixRegex.findFirstIn(previous).get.length + 3
//    val actualIndent = "^ *".r.findFirstIn(next).get.length
//    if (actualIndent <= allowableIndent) true
//    else false
//  } else false

  private def checkListItemAfterItem(previous: String, next: String) = anyMatchingRegex(next, Seq(blockQuoteRegex, emptyLineRegex)).isDefined



}

protected[parsing] class MdParsingBot(string: String) extends MdParsing {
  override val lines: List[String] = string.split("(\\n|\\r)").toList
  override val factory: MdFactory = new MdFactory {}
}
