package com.gray.util.attributed_string

import com.gray.util.Ranj

import scala.collection.mutable
import scala.util.matching.Regex

abstract class AbstractAttributedString[+This](_string: String, _attributes: List[Attribute]) {
  self: This =>
  val string = _string
  val attributes: AttributeList = new AttributeList(_attributes)

  val length = string.length

  protected[attributed_string] def newThis(str: String, atts: List[Attribute]): This

  def attributesInRange(range: Ranj) = attributes.flatMap(_.copyInRange(range)).toList

  def addAttribute(formats: Seq[String], range: Ranj = Ranj(0, string.length)) = {
    val newAtt = Attribute(range, formats)
    val newattributes = attributes.mergeAttribute(newAtt)
    newThis(_string, newattributes)
  }

  def addAttributeForRegex(regex: Regex, atts: Seq[String]) = {
    var outgoingAttributes = attributes
    regex.findAllMatchIn(string).foreach { `match` =>
      val attribute = Attribute(Ranj(`match`.start, `match`.end), atts)
      outgoingAttributes = new AttributeList(outgoingAttributes.mergeAttribute(attribute))
    }
    newThis(_string, outgoingAttributes.toList)
  }

  def addAttributeForRegexGroups(regex: Regex, groupAtts: Map[Int, Seq[String]]) = {
    var outgoingAttributes = attributes
    regex.findAllMatchIn(string).foreach { `match` =>
      groupAtts.foreach { tuple =>
        val (group, att) = (tuple._1, tuple._2)
        val groupRange = Ranj(`match`.start(group), `match`.end(group))
        val attribute = Attribute(groupRange, att)
        outgoingAttributes = new AttributeList(outgoingAttributes.mergeAttribute(attribute))
      }
    }
    newThis(_string, outgoingAttributes.toList)
  }

  def addAttributeForRegexGroupsInRanges(ranges: List[Ranj], regex: Regex, groupAtts: Map[Int, Seq[String]]) = {
    var outgoingAttributes = attributes
    regex.findAllMatchIn(string).foreach { `match` =>
      val matchRange = Ranj(`match`.start, `match`.end)
      if (ranges.exists(matchRange.isSubsetOf)) {
        groupAtts.foreach { tuple =>
          val (group, att) = (tuple._1, tuple._2)
          val groupRange = Ranj(`match`.start(group), `match`.end(group))
          val attribute = Attribute(groupRange, att)
          outgoingAttributes = new AttributeList(outgoingAttributes.mergeAttribute(attribute))
        }
      }
    }
    newThis(_string, outgoingAttributes.toList)
  }

  def substring(start: Int, end: Int = string.length): This = {
    val str = string.substring(start, end)
    val attributes = attributesInRange(Ranj(start, end))
    newThis(str, attributes)
  }
  def substring(range: Ranj): This = substring(range.start, range.end)

  def segment(regex: Regex) = {
    var out = List.empty[This]
    val matches = regex.findAllMatchIn(string)
    var mark = 0
    matches.foreach { mtch =>
      val range = Ranj(mtch.start, mtch.end)
      if (range.start > mark) out = out :+ substring(mark, range.start)
      out = out :+ substring(range)
      mark = range.end
    }
    if (mark < length) out = out :+ substring(mark, length)
    out
  }

  def splitLines = {
    val buffer = new mutable.MutableList[This]()
    var marker = 0
    while (string.indexOf('\n', marker) match {
      case -1 =>
        buffer += substring(marker)
        false
      case i =>
        buffer += substring(marker, i)
        marker = i + 1
        true
    }) {}
    buffer.toList
  }

  def split(regex: String) = ???


}
