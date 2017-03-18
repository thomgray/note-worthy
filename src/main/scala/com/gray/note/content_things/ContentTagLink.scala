package com.gray.note.content_things

import com.gray.markdown.MdLocation

class ContentTagLink( override val path: String = "") extends ContentTagLikeThing {
  override def isParaphrase: Boolean = ???

  override def getString: String = ???

  override val location: MdLocation = ???
  override val labels: List[String] = ???
}
