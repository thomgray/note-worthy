package com.gray.parse.mdparse

import com.gray.markdown.MdHeader
import com.gray.markdown.parsing.{MdParser, MdRegexes}
import com.gray.parse.{ContentParser, Location, ParseResult}

object MdIterator extends ContentParser {

  def nextThingFrom(marker:Int, linesArray: Array[String], offset: Int) = {
    rageOfNextHeaderBlock(marker, linesArray) match {
      case Some((i, j, header)) if i == marker =>
        Some(
          ParseResult(linesArray.slice(i+1, j).mkString("\n"),
            Some(List(header.string.toLowerCase)),
            CONTENT_TAG,
            "",
            Location(i + offset, j + offset)),
          j
        )
      case Some((i,j,header)) =>
        val str = linesArray.slice(marker, i).mkString("\n")
        Some(
          ParseResult(
            str,
            None,
            CONTENT_STRING,
            "",
            Location(marker+offset, i+offset)
          ),
          i
        )
      case None if marker < linesArray.length =>
        val str = linesArray.slice(marker, linesArray.length).mkString("\n")
        Some(
          ParseResult(
            str,
            None,
            CONTENT_STRING,
            "",
            Location(marker, linesArray.length)
          ),
          linesArray.length
        )
      case _ => None
    }
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

  def readMdHeader(line: String) = {
    line match {
      case trimmedHeaderRegex(match1, match2) => Some(MdHeader(match2, match1.length))
      case _ => None
    }
  }

  override def apply(string: String, linesOffset: Int): List[ParseResult] = {
    var marker = 0
    val lines = string.split("(\\n|\\r)")
    var list = List.empty[ParseResult]
    while (nextThingFrom(marker, lines, linesOffset) match {
      case Some((res, newMarker)) =>
        list = list :+ res
        marker = newMarker
        true
      case None => false
    }){}
    list
  }

}
