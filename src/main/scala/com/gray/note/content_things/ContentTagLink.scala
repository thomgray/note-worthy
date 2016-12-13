package com.gray.note.content_things

import com.gray.parse.ParseResult

class ContentTagLink(result: ParseResult, path: String = "") extends ContentTagLikeThing(result) {
  override def isParaphrase: Boolean = ???

  override def getString: String = ???

  override val filePath: String = path
}
