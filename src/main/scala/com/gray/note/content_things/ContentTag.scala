package com.gray.note.content_things

import com.gray.markdown.MdParagraph
import com.gray.parse.{Location, ParseResult}
import com.gray.util.Formatting

import scala.reflect.ClassTag

class ContentTag(result: ParseResult, path: String = "") extends ContentTagLikeThing(result) with Formatting {

  private var _contents: List[Content] = List.empty

  val location = result.location
  override val filePath = path

  private var _linkName: Option[String] = None
  def setLinkName(string: String) = _linkName = Some(string)
  def linkName = _linkName
  def isLinked = _linkName.isDefined


  private[content_things] def setContents(contents: List[Content]) = _contents = contents

  def getTagContents = _contents.filter(t => t.isInstanceOf[ContentTag]).asInstanceOf[List[ContentTag]]
//  def getTagContents = _contents.filter{
//    case _ :ContentTag => true
//    case _ => false
// }  //TODO this might be better

  def get[T <: Content : ClassTag] = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    _contents.filter(clazz.isInstance(_)).asInstanceOf[List[T]]
  }

  def getContents = _contents

  def getMdParagraphs: List[MdParagraph] = getContents.flatMap {
    case string: ContentString => string.paragraphs().getOrElse(List.empty)
    case tag: ContentTag => tag.getMdParagraphs
  }

  override def isParaphrase: Boolean = false

  override def getAllDescendantContent: List[Content] = this :: _contents.flatMap(_.getAllDescendantContent)

  def getAllNestedTags: List[ContentTag] = this :: getTagContents.flatMap(_.getAllNestedTags)

  def getLinearHierarchy: List[ContentTag] = parentTag match {
    case None => List(this)
    case Some(parent) => parent.getLinearHierarchy :+ this
  }

  override def getString: String = {
    val includedContent = _contents.filter {
      case tag: ContentTagLikeThing => tag.isParentVisible
      case _ => true
    }
    (includedContent map {
      case tag: ContentTag =>
        val title = tag.getTitleString
        val body = indentString(tag.getString)
        UNDERLINED + title + RESET + "\n" + body
      case other => other.getString
    }).mkString("\n\n")
  }
}