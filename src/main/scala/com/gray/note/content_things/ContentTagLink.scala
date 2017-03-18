package com.gray.note.content_things

import com.gray.markdown.{@@, MdLocation}
import com.gray.parse.ParseResult

class ContentTagLink(result: ParseResult, override val path: String = "") extends ContentTagLikeThing {
  override def isParaphrase: Boolean = ???

  override def getString: String = ???

  override val location: MdLocation = ???
}
