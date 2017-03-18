package com.gray.parse

import com.gray.markdown.{MdHeader, MdParagraph}

trait ContentParser extends ParseConstants {
  def apply(string: String, format: String): List[AbstractParseResult]
}


case class Location(lineStart: Int, lineEnd: Int, columnStart: Int = 0, columnEnd: Int = 0)

abstract class AbstractParseResult

case class TagParseResult(content: List[AbstractParseResult], header: MdHeader, altLabels: List[String]) extends AbstractParseResult

case class StringParseResult(paragraphs: List[MdParagraph]) extends AbstractParseResult
case class AliasParseResult()

trait ParseConstants {
  val CONTENT_TAG = "tag"
  val CONTENT_STRING = "string"
  val CONTENT_ALIAS = "alias"

  val PARENT_VISIBLE_FLAG = "^"
  val UNIVERSAL_REFERENCE_FLAG = "*"
  val CONTENT_INVISIBLE_FLAG = "-"
}
