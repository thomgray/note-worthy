package com.gray.parse

protected[gray] abstract class ParseIterator extends ParseConstants {

  def nextThing: Option[ParseResult]

}

case class ParseResult( string: String,
                        labels: Option[List[String]],
                        description: String,
                        options: String )


trait ParseConstants {
  val CONTENT_TAG = "tag"
  val CONTENT_STRING = "string"
  val CONTENT_ALIAS = "alias"

  val PARENT_VISIBLE_FLAG = "^"
  val UNIVERSAL_REFERENCE_FLAG = "*"
}
