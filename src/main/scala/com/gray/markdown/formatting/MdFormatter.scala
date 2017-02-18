package com.gray.markdown.formatting

import com.gray.markdown._
import com.gray.markdown.parsing.Regexes
import com.gray.util.DefaultFormatter
import com.gray.util.attributed_string.AttributedString

import scala.io.AnsiColor

class MdFormatter(linkRefs: List[MdLinkRef]) extends DefaultFormatter with MdCharacterConstants with CodeColouring with Regexes {


  def renderQuoteBlock(quote: MdQuoteBlock, width: Int) = {
    val string = formatString(quote.string, width-3)
    string.split("\n").map(s => WHITE_B + " " + RESET +" "+ s).mkString("\n")
  }

  def renderHeader(header: MdHeader, width: Int) = {
    val headString = header.string
    val headerLength = header.string.length
    (header.size match {
      case 5 => BOLD + header.string
      case 4 => BOLD + header.string.toUpperCase
      case 3 => UNDERLINED + BOLD + headString.toUpperCase
      case 2 => BOLD + headString.toUpperCase + "\n" + concatenate("─", headerLength)
      case 1 => BOLD + headString.toUpperCase + "\n" + concatenate("═", headerLength)
    }) + RESET
  }

  def renderCodeBlock(code: MdCodeBlock, width: Int) = {
    var str = AttributedString(code.string, Seq(BLACK_B, WHITE, BOLD))
    str = code.syntax match {
      case Some("scala") => colourScala(str)
      case _ => str
    }

    str = str.wrapLines(width-4, Some(AttributedString("⤥", Seq(BLACK_B, WHITE))), Some(AttributedString("⤦", Seq(BLACK_B, WHITE))))
    var lines = str.splitLines
    val longestLine = lines.foldRight(0)((line, i) => if (line.length>i) line.length else i) + 1
    lines = lines.map(AttributedString(" ") + AttributedString(" ", Seq(BLACK_B, WHITE, BOLD)) + _.padTo(longestLine, " ", Seq(BLACK_B, WHITE, BOLD)))
    AttributedString.mkStringWithLines(lines).toString
  }

  def renderHorizonalLine(width: Int) = concatenate("─", width)


  def renderParagraph(paragraph: MdParagraph, width: Int): String = paragraph match {
    case list: MdList => renderList(list, width)
    case string: MdString => renderString(string, width)
    case literal: MdLiteral => renderLiteral(literal, width)
    case code: MdCodeBlock => renderCodeBlock(code, width)
    case quote: MdQuoteBlock => renderQuoteBlock(quote, width)
    case header: MdHeader => renderHeader(header, width)
    case hLine: MdHorizontalLine => renderHorizonalLine(width)
    case MdPlainString(plainString) => formatString(plainString, width)
    case other => "*** HAVEN'T IMPLEMENTED RENDERING FOR THIS YET ***\n"+other.toString
  }

  def renderList(list: MdList, width: Int)= {
    var str = ""
    for (item <- list.items) {
      val markLength = 5
      val textWidth = width -  markLength - 1
      val stringBody = item.items.map(renderParagraph(_, textWidth)).mkString("\n")
      val bullet = item match {
        case MdCheckListItem(_,checked) => "   " + (if (checked) BOX_CHECKED_GREEN else BOX_UNCHECKED) + " "
        case MdBulletListItem(_) => "   " + bulletForTier(item.tier) + " "
        case MdNumberListItem(_,number) =>
          val convertedNumber = item.tier match {
            case 0 => number.toString
            case _ => lowercaseRoman(number)
          }
          padSingleLine(s"$BLUE$convertedNumber$RESET.", markLength, "right")
      }
      val marker = bullet + " "
      str += "\n" + stitchString(List(marker, stringBody))
    }
    str.substring(1)
  }

  def renderString(string: MdString, width: Int) = {
    //todo do something with url regexes
    //todo inline formatting

    var attributedString = AttributedString(string.string)
    string.links.reverse.foreach { tuple =>
      val (link, range) = tuple
      val linkStr = AttributedString(link.inlineString.getOrElse(link.url), Seq(BLUE))
//      val linkStr = (attributedString.substring(range) + AttributedString(subscript(link.index))).addAttribute(Seq(BLUE))
      attributedString = attributedString.substring(0, range.start) + linkStr + attributedString.substring(range.end)
    }
    attributedString.wrapLines(width).toString
  }

  def renderLiteral(literal: MdLiteral, width: Int) = {
    val strlines = padAndAlignBlock(formatString(literal.string, width-2)).split("\n")
    strlines.map(line => " " + WHITE_B + BLACK + " " + line + RESET).mkString("\n")
  }

}

trait MdCharacterConstants {
  val BULLET1 = "•"
  val BULLET2 = "◦"
  val BULLET3, DASH = "⁃"
  val BULLET4 = "∙"

  val BOX_CHECKED = "☒"
  val BOX_CHECKED_GREEN = AnsiColor.GREEN + "☒" + AnsiColor.RESET
  val BOX_UNCHECKED = "☐"

  def bulletForTier(tier: Int) = tier match {
    case 0 => BULLET1
    case 1 => BULLET2
    case 2 => BULLET3
    case _ => BULLET4
  }

  def H_LINE(width: Int) = (for (_ <- 0 until width) yield "─").mkString

  val romansLCase = Map(1 -> "i", 4 -> "iv", 5 -> "v", 9 -> "ix",10 -> "x",40 -> "xl", 50 -> "l", 90 -> "xc",100 -> "c")
  val romanDenominations = List(100,90,50,40,10,9,5,4,1)

  def lowercaseRoman(int: Int) = {
    var counter = int
    var str = ""
    while (counter > 0) {
      val denominator = romanDenominations.find(_ <= counter).get
      counter -= denominator
      str += romansLCase(denominator)
    }
    str match {
      case "" => "0"
      case other => other
    }
  }
}

object MdFormatter {
  def apply(linkRefs: List[MdLinkRef] = List.empty): MdFormatter = new MdFormatter(linkRefs)
}

