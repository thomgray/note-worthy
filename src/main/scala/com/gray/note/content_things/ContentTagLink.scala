package com.gray.note.content_things

import com.gray.markdown.@@
import com.gray.parse.ParseResult

class ContentTagLink(result: ParseResult, path: String = "") extends ContentTagLikeThing(@@(0,0)) {
  override def isParaphrase: Boolean = ???

  override def getString: String = ???

  override val filePath: String = path
}
