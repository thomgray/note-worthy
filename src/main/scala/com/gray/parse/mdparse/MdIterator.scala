package com.gray.parse.mdparse

import _root_.com.gray.markdown._
import com.gray.markdown.produce.MdParser
import com.gray.note.content_things.MdAlias
import com.gray.parse._

import scala.util.control.Breaks._

object MdIterator extends ContentParser {
  val aliasRegex = """^\[(.+)\] *<(.*)>$""".r

  val mdparser = new MdParser {
    override val checks: List[(List[String], Int, Int) => Option[(MdParagraph, Int)]] = defaultChecks ++ List(
      (lines: List[String], marker: Int, offset: Int) => aliasRegex.findFirstMatchIn(lines(marker)) map { mtch =>
        (MdAlias(mtch.group(2), mtch.group(1), @@(0,0)), marker+1)
      }
    )
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
            content = content :+ TagParseResult(tagContent, header, Nil)
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

  private val trimmedHeaderRegex = "(#{1,5}) +(.*)".r

  override def apply(string: String): List[AbstractParseResult] = {
    val document = mdparser.parse(string)
    getMdContentPart(document.paragraphs)
  }

}
