package com.gray.parse.mdparse

import com.gray.markdown.MdHeader
import com.gray.parse.{ContentParser, Location, ParseResult}

object MdIterator extends ContentParser {

  def nextThingFrom(marker:Int, linesArray: Array[String], offset: Int): Option[(List[ParseResult], Int)] = {
    rageOfNextHeaderBlock(marker, linesArray) match {
      case Some((i, j, header)) if marker < i && linesAreNonBlank(linesArray.slice(marker, i)) =>
//        val str = linesArray.slice(marker, i).mkString("\n")
//        Some(
//          ParseResult(
//            str,
//            None,
//            CONTENT_STRING,
//            "",
//            Location(marker+offset, i+offset)
//          ),
//          i
//        )
        val list = splitStringIntoStringAlias(marker, i, linesArray, offset)
        Some(list, i)
      case Some((i,j,header)) =>
        Some(List(handleContentTag(i,j,header,linesArray, offset)), j)
      case None if marker < linesArray.length =>
        val list = splitStringIntoStringAlias(marker, linesArray.length, linesArray, offset)
        Some(list, linesArray.length)
//        val slice = linesArray.slice(marker, linesArray.length)
//        if (linesAreNonBlank(slice)) {
//          val str = slice.mkString("\n")
//          Some(
//            ParseResult(
//              str,
//              None,
//              CONTENT_STRING,
//              "",
//              Location(marker, linesArray.length)
//            ),
//            linesArray.length
//          )
//        }else None
      case _ => None
    }
  }

  val aliasRegex = """^\[(.+)\] *<(.*)>$""".r

  def getAlias(string: String, i: Int) = string match {
    case aliasRegex(group1, group2) =>
      val labels = group1.split(";").map(_.toLowerCase).toList
      Some(ParseResult(group2, Some(labels), CONTENT_ALIAS, "", Location(i, i)))
    case _ => None
  }

  def rageOfNextHeaderBlock(from: Int, linesArray: Array[String]) = {
    indexOfNextHeader(from, linesArray) match {
      case Some((i, header)) =>
        val end = indexOfNextHeader(i + 1, linesArray, header.size) match {
          case Some((j, header2)) => j
          case None => linesArray.length
        }
        Some((i, end, header))
      case None => None
    }
  }

  def indexOfNextHeader(from: Int, linesArray: Array[String], tier: Int = 0): Option[(Int, MdHeader)] = {
    for (i <- from until linesArray.length) {
      readMdHeader(linesArray(i)) match {
        case Some(header) if header.size <= tier | tier == 0 => return Some((i, header))
        case _ =>
      }
    }
    None
  }

  private val trimmedHeaderRegex = "(#{1,5}) +(.*)".r

  def splitStringIntoStringAlias(from: Int, to: Int, lines: Array[String], offset: Int) = {
    var list : List[ParseResult] = List.empty
    var marker = from
    while (nextAlias(marker, to, lines, offset) match {
      case Some((result, aliasIndex)) =>
        val addition = marker < aliasIndex match {
          case true => Some(handleString(marker, aliasIndex, lines, offset))
          case false => None
        }
        list = list ++ addition :+ result
        marker = aliasIndex + 1
        true
      case None =>
        if (marker < to) {
          list = list :+ handleString(marker, to, lines, offset)
        }
        false
    }){}
    list
  }

  def nextAlias(from: Int, to: Int, lines: Array[String], offset: Int): Option[(ParseResult, Int)]= {
    for (i <- from until to) {
      getAlias(lines(i), i+offset) match {
        case Some(aliasResult) =>
          return Some(aliasResult, i)
        case _ =>
      }
    }
    None
  }

  def handleString(from: Int, to: Int, lines: Array[String], offset: Int) = {
    val string = lines.slice(from, to).mkString("\n")
    ParseResult(string, None, CONTENT_STRING, "", Location(from+offset, to+offset))
  }

  def readMdHeader(line: String) = {
    line match {
      case trimmedHeaderRegex(match1, match2) => Some(MdHeader(match2, match1.length))
      case _ => None
    }
  }

  private def linesAreNonBlank(array: Array[String]) = {
    array.exists(!_.matches("\\s*"))
  }


  private val optionalLabelsRegex = "^(.*?)(?:\\[(.*)\\])$".r

  private def handleContentTag(start: Int, end: Int, header: MdHeader, lines: Array[String], offset: Int) = {
    val string = lines.slice(start+1, end).mkString("\n")
    val location = Location(start+offset, end+offset)

    val labels = header.string match {
      case `optionalLabelsRegex`(title, optionals) =>
        val optionalsList = optionals.split(";").toList.map(_.trim.toLowerCase)
        title.trim.toLowerCase +: optionalsList
      case _ =>
        List(header.string.trim.toLowerCase)
    }
    ParseResult(string, Some(labels), CONTENT_TAG, "", location)
  }

  override def apply(string: String, linesOffset: Int): List[ParseResult] = {
    var marker = 0
    val lines = string.split("(\\n|\\r)")
    var list = List.empty[ParseResult]
    while (nextThingFrom(marker, lines, linesOffset) match {
      case Some((res, newMarker)) =>
        list = list ++ res
        marker = newMarker
        true
      case None => false
    }){}
    list
  }

}
