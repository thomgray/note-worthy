package com.gray.note.content_things

import com.gray.markdown.MdLocation
import com.gray.parse.{AbstractParseResult, ParseConstants, ParseResult}

class ContentTagAlias( val alias: String,
                       val labels: Seq[String],
                       location: MdLocation,
                       val path: String = "") extends ContentTagLikeThing(location){

  override def isParaphrase: Boolean = true

  override def getString: String = alias

  def getAliasedQuery = parentTag match {
    case Some(parent) => s"${parent.getQueryString} $getString"
    case None => getString
  }

  override val filePath: String = path
}

object ContentTagAlias extends ParseConstants {
//  def apply(string: String, labels: List[String], path: String = "") = new ContentTagAlias(ParseResult(string, Some(labels), CONTENT_ALIAS, ""), path)
}