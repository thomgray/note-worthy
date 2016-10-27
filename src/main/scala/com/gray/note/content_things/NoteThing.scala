package com.gray.note.content_things

import com.gray.note.parsing.TagParser
import com.gray.note.{AttributedString, Config, Formatter}

import scala.collection.mutable.ArrayBuffer
import scala.io.AnsiColor

abstract class Content(rawString: String) extends AnsiColor {
  var parentTag: Option[ContentTag] = None

  def getAllDescendantContent: Array[Content] = List(this).toArray[Content]

  def formatAndIndentString(indent: Int): String = {
    val output = Formatter.indent(rawString, indent)
    Formatter.handleEscapeStrings(output)
  }

  override def toString: String =  Formatter.handleEscapeStrings(rawString)
  def getAttributesString = new AttributedString(Formatter.handleEscapeStrings(rawString))
}

class ContentString(rawString: String) extends Content(rawString) {
  def isContinuedOnLine = rawString.startsWith(":")

  override def toString: String = {
    if (rawString.startsWith(":")) Formatter.handleEscapeStrings(rawString.substring(1))
    else super.toString
  }
}




