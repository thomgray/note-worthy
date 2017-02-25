package com.gray.parse

abstract class ParseIterator(linesOffset: Int = 0) extends ParseConstants {

  def nextThing: Option[ParseResult]

  def iterate: List[ParseResult] = {
    var list = List.empty[ParseResult]
    while (nextThing match {
      case Some(res) =>
        list = list :+ res
        true
      case None => false
    }){}
    list
  }
}

trait ContentParser extends ParseConstants {
  def apply(string: String, linesOffset: Int = 0): List[ParseResult]
}

case class ParseResult( string: String,
                        labels: Option[List[String]],
                        description: String,
                        options: String = "",
                        location: Location = Location(0,0)
                      )

case class Location(lineStart: Int, lineEnd: Int, columnStart: Int = 0, columnEnd: Int = 0)


trait ParseConstants {
  val CONTENT_TAG = "tag"
  val CONTENT_STRING = "string"
  val CONTENT_ALIAS = "alias"

  val PARENT_VISIBLE_FLAG = "^"
  val UNIVERSAL_REFERENCE_FLAG = "*"
  val CONTENT_INVISIBLE_FLAG = "-"
}
