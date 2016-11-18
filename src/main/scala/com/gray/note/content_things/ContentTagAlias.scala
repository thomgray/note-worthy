package com.gray.note.content_things

import com.gray.parse.ParseResult

class ContentTagAlias(result: ParseResult) extends ContentTagLikeThing(result){

  override def isParaphrase: Boolean = true

  override def getString: String = result.string.trim
}