package com.gray.markdown.parsing

import com.gray.markdown._
import com.gray.util.{Formatting, Ranj}

import scala.util.matching.Regex

protected[parsing] abstract class MdParsing extends MdParsingRuleBase with MdRegexes with Formatting {

  val factory: MdFactory
  import factory._

  def parse() = {
    while (marker < lines.length) {
      var result: Option[MdParagraph] = None

      checks.find { check =>
        result = check()
        result.isDefined
      }

      result match {
        case None => stringBuffer += getLine
        case Some(mtch) =>
          flushStringBuffer
          paragraphs += mtch
      }
      marker += 1
      if (marker == lines.length) flushStringBuffer
    }
  }

  val checks = List[() => Option[MdParagraph]](
    checkEmptyLineBlock,
    checkHeader,
    checkHorizontalLine,
    checkCodeBlock,
    checkIndentedLiteral,
    checkList,
    checkBlockQuote
  )

  def flushStringBuffer = if (stringBuffer.nonEmpty) {
    paragraphs += makeMdString(stringBuffer.toList, linkRefs)
    stringBuffer.clear
  }

  def checkHorizontalLine() = if (lineMatchesRegex(horizontalLineRegex, getLine)) {
    Some(MdHorizontalLine())
  } else None

  def checkHeader() = if (lineMatchesRegex(headerRegex)) {
    Some(makeMdHeader(getLine))
  } else None

  def checkCodeBlock() = getRangeOfSpanRegex(codeBeginRegex, codeEndRegex) match {
    case Some(range) =>
      marker = range.end - 1
      val lines = getLines(range)
      Some(makeMdCodeBlock(lines))
    case None => None
  }

  def checkEmptyLineBlock() = getRangeOfSolidBlock(emptyLineRegex) match {
    case Some(range) =>
      marker = range.end - 1
      Some(MdBreak())
    case None => None
  }

//  def checkIndentedLiteral() = getRangeOfSolidBlock(indentedLiteralRegex) match {
//    case Some(range) =>
//      marker = range.end - 1
//      Some(makeMdIndentedLiteral(getLines(range)))
//    case None => None
//  }

  def checkIndentedLiteral() = { // needs word still apparently
    def indexOfEndOfIndent(from: Int): Option[Int] = {
      getRangeOfSolidBlock(indentedLiteralRegex, from) match {
        case Some(ranj) =>
          getRangeOfSolidBlock(emptyLineRegex, ranj.end) match {
            case Some(emptyRanj) =>
              indexOfEndOfIndent(emptyRanj.end) match {
                case Some(nextIndentEnd) =>
                  Some(nextIndentEnd)
                case None => Some(ranj.end)
              }
            case None => Some(ranj.end)
          }
        case None => None
      }
    }

    indexOfEndOfIndent(marker) match {
      case Some(end) =>
        val range = Ranj(marker, end)
        marker = end-1
        Some(makeMdIndentedLiteral(getLines(range)))
      case None => None
    }
  }

  def checkBlockQuote() = lineMatchesRegex(blockQuoteRegex) match {
    case false => None
    case true =>
      val range = lines.indexWhere({ ln =>
        lineMatchesAnyRegex(ln, emptyLineRegex, bulletListItemRegex, checkListItemRegex)
      }, marker + 1) match {
        case -1 => Ranj(marker, lines.length)
        case other => Ranj(marker, other)
      }
      marker = range.end - 1
      Some(makeMdQuoteBlock(getLines(range)))
  }

  def checkBulletList() = checkListGeneral(bulletListItemRegex)

  def checkNumberList() = checkListGeneral(numberedListItemRegex)

  def checkCheckboxList() = checkListGeneral(checkListItemRegex)

  def checkList() = {
    val regexes = List(checkListItemRegex, bulletListItemRegex, numberedListItemRegex)
    var result: Option[MdList] = None
    regexes.find { regex =>
      checkListGeneral(regex) match {
        case res@Some(par) => result = res
          marker -= 1     /*  decrement marker as we will have overshot it.
                              We're now sitting on the next paragraph, but
                              we want to be at the end of this paragraph  */
          true
        case _ => false
      }
    }
    result
  }

  private def checkListGeneral(itemRegex: Regex) = {
    var itemPackets = List.empty[String]
    while (marker < lines.length && (checkListItem(itemRegex) match {
      case Some(str) => itemPackets = itemPackets :+ str
        marker += 1 // must increment marker for next check, but this puts us over so we decrement later
        true
      case None => false
    })) {}
    (itemPackets, itemRegex) match {
      case (list, _) if list.isEmpty => None
      case (list, `bulletListItemRegex`) =>
        Some(makeMdBulletList(list))
      case (list, `checkListItemRegex`) =>
        Some(makeMdCheckboxList(list))
      case (list, `numberedListItemRegex`) =>
        Some(makeMdNumberList(list))
      case (_, regex) => throw new RuntimeException(s"Must call this method with bullet, checkbox or number list item regex, you provided $regex")
    }
  }

  def checkListItem(itemRegex: Regex) = lineMatchesRegex(itemRegex) match {
    case false => None
    case true =>
      val lengthOfIndent = bulletOrNumberPrefix.findFirstIn(getLine).get.length
      val whitespacePrefix = concatenate(" ", lengthOfIndent)
      var lastLineBlank = false
      val range = lines.indexWhere({ str =>
        val thisLineBlank = lineMatchesRegex(emptyLineRegex, str)
        val breakDueToDoubleBlank = thisLineBlank && lastLineBlank
        lastLineBlank = thisLineBlank
        breakDueToDoubleBlank || !(str.startsWith(whitespacePrefix) || thisLineBlank)
      }, marker + 1) match {
        case -1 => Ranj(marker, lines.length)
        case other => Ranj(marker, other)
      }
      val firstLine = getLine.substring(lengthOfIndent)
      val rest = getLines(range.start + 1, range.end).map(_.stripPrefix(whitespacePrefix))
      marker = range.end - 1
      Some((firstLine :: rest).mkString("\n"))
  }

}

protected[parsing] class MdParsingBot(string: String) extends MdParsing {
  override val factory: MdFactory = new DefaultMdFactory
  override protected[parsing] val docString: String = string
}
