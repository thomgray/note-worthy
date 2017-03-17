package com.gray.note.content_things

import com.gray.markdown.{MdHeader, MdLocation, MdParagraph}
import com.gray.parse.{AbstractParseResult, Location, ParseConstants, ParseResult}
import com.gray.note.util.Formatting

import scala.reflect.ClassTag

class ContentTag(val contents: List[Content],
                 val header: MdHeader,
                 val altLabels: List[String],
                 location: MdLocation,
                 val path: String = "") extends ContentTagLikeThing(location) with Formatting {

  contents.foreach(_.setParent(Some(this)))
//  private var _contents: List[Content] = List.empty

//  val location = result.location
//  override val filePath = path

  override def getLabels: List[String] = header.mdString.string.trim +: altLabels

  private var _linkName: Option[String] = None

  def setLinkName(string: String) = _linkName = Some(string)

  def linkName = _linkName

  def isLinked = _linkName.isDefined


//  private[content_things] def setContents(contents: List[Content]) = _contents = contents

  def getTagContents = contents collect {
    case ct: ContentTag => ct
  }

  def getTakLikeContents = contents.collect {
    case t: ContentTag => t
    case t: ContentTagAlias => t
  }

  def get[T <: Content : ClassTag] = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    contents.filter(clazz.isInstance(_)).asInstanceOf[List[T]]
  }

//  def getContents = _contents

//  def getMdParagraphs: List[MdParagraph] = contents.flatMap {
//    case string: ContentString => string.paragraphs
//    case tag: ContentTag => tag.getMdParagraphs
//  }

  override def isParaphrase: Boolean = false

  override def getAllDescendantContent: List[Content] = this :: contents.flatMap(_.getAllDescendantContent)

  def getAllNestedTags: List[ContentTag] = this :: getTagContents.flatMap(_.getAllNestedTags)

  def getAllNestedTagLikeThings: List[ContentTagLikeThing] = this :: getTakLikeContents.flatMap {
    case ct: ContentTag => ct.getAllNestedTagLikeThings
    case ca: ContentTagAlias => List(ca)
  }

  def getLinearHierarchy: List[ContentTag] = parentTag match {
    case None => List(this)
    case Some(parent) => parent.getLinearHierarchy :+ this
  }

  override def getString: String = {
    val includedContent = contents.filter {
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

  override val filePath: String = path
}

object ContentTag extends ParseConstants {
//  def apply(content: String, labels: List[String], location: Location, innerContent: List[Content] = Nil, options: String = "", path: String = "") = {
//    val tag = new ContentTag(ParseResult(content, Some(labels), CONTENT_TAG, options, location), path)
//    tag.setContents(innerContent)
//    innerContent.foreach(_.setParent(Some(tag)))
//    tag
//  }

}