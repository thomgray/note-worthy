package com.gray.note.content_things

import com.gray.parse.{ParseConstants, ParseResult}

class ContentTagAlias(result: ParseResult, path: String = "") extends ContentTagLikeThing(result){

  override def isParaphrase: Boolean = true

  override def getString: String = result.string.trim

  def getAliasedQuery = parentTag match {
    case Some(parent) => s"${parent.getQueryString} $getString"
    case None => getString
  }

  override val filePath: String = path
}

object ContentTagAlias extends ParseConstants {
  def apply(string: String, labels: List[String], path: String = "") = new ContentTagAlias(ParseResult(string, Some(labels), CONTENT_ALIAS, ""), path)
}