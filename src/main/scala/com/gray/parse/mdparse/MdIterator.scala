package com.gray.parse.mdparse

import _root_.com.gray.markdown._
import com.gray.markdown.produce.MdParser
import com.gray.markdown.produce.parsingrules.MdParsingRule
import com.gray.note.content_things.{MdAlias, MdAliasParsingRule}
import com.gray.parse._

import scala.util.control.Breaks._

object MdIterator extends ContentParser {
  protected[mdparse] val headerAltLabelsRegex = """^\s*(.+?)\s*\[(.+)\]\s*$""".r

  val mdparser = new MdParser {
    override val checks: List[MdParsingRule] = defaultChecks :+ MdAliasParsingRule
  }

  def getMdContent(string: String, path: String, format: String) = {
    val document = mdparser.parse(string)
    getMdContentPart(document.paragraphs)
  }

  protected def getMdContentPart(paragraphs: List[MdParagraph]): List[AbstractParseResult] = {
    var content: List[AbstractParseResult] = paragraphs.takeWhile(!_.isInstanceOf[MdHeader]) match {
      case Nil => Nil
      case list => List(StringParseResult(list))
    }
    var mark = 0
    breakable {
      while (true) {
        rangeOfNextHeaderBlock(paragraphs, 0, mark) match {
          case Some((header, start, end)) =>
            val tagContent =getMdContentPart(paragraphs.slice(start+1, end))
            val (realHeader, labels) = getAlLablesFromHeader(header)
            content = content :+ TagParseResult(tagContent, realHeader, labels)
            mark = end
          case None =>
            break
        }
      }
    }
    content
  }

  private def rangeOfNextHeaderBlock(paragraphs: List[MdParagraph], value: Int, from: Int) = {
    var realValue = value
    paragraphs.indexWhere({
      case MdHeader(_,hValue,_) =>
        val check = hValue >= realValue
        if (check) realValue = hValue
        check
      case _ => false
    }, from) match {
      case -1 => None
      case other =>
        val endIndex = paragraphs.indexWhere({
          case MdHeader(_,hvalue,_) => hvalue <= realValue
          case _ => false
        }, other+1) match {
          case -1 => paragraphs.length
          case other2 => other2
        }

        val header = paragraphs(other).asInstanceOf[MdHeader]
        Some(header, other, endIndex)
    }
  }

  protected[mdparse] def getAlLablesFromHeader(header: MdHeader) = {
    val rawString = header.mdString.string
    headerAltLabelsRegex.findFirstMatchIn(rawString) match {
      case Some(mtch) =>
        (
          MdHeader(MdString(mtch.group(1), header.mdString.location), header.value, header.location),
          mtch.group(2).split(";").map(_.trim.toLowerCase).toList
        )
      case _ =>
        (header, Nil)
    }
  }

  override def apply(string: String, format: String = "md"): List[AbstractParseResult] = {
    val document = mdparser.parse(string)
    getMdContentPart(document.paragraphs)
  }

}
