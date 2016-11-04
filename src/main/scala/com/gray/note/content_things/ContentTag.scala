package com.gray.note.content_things

import com.gray.parse.ParseResult
import com.gray.util.Formatting

class ContentTag(result: ParseResult) extends ContentTagLikeThing(result) with Formatting {

  private var _contents: List[Content] = List.empty

  private[content_things] def setContents(contents: List[Content]) = _contents = contents
  def getContents = _contents
  def getTagContents = _contents.filter(t=>t.isInstanceOf[ContentTag]).asInstanceOf[List[ContentTag]]

  override def isParaphrase: Boolean = false

  override def getAllDescendantContent: List[Content] = _contents.flatMap(_.getAllDescendantContent).::(this)

  def getAllNestedTags: List[ContentTag] = this +: getTagContents.flatMap(_.getAllNestedTags)

  override def getString: String = {
    val includedContent = _contents.filter{
      case tag: ContentTagLikeThing => tag.isParentVisible
      case _ => true
    }
    (includedContent map {
      case tag : ContentTag =>
        val title = tag.getTitleString
        val body = indentString(tag.getString)
        UNDERLINED + title + RESET +"\n"+body
      case other => other.getString
    }).mkString("\n\n")
  }
}