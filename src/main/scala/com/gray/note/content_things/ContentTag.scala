package com.gray.note.content_things

import com.gray.markdown.{MdHeader, MdLocation}
import com.gray.note.util.Formatting
import com.gray.parse.ParseConstants

import scala.reflect.ClassTag

class ContentTag(val contents: List[Content],
                 val header: MdHeader,
                 val altLabels: List[String],
                 override val path: String = "") extends ContentTagLikeThing with Formatting {

  override val location = {
    val (endLn, endCol) = contents.lastOption match {
      case Some(last) => (last.location.endLine, last.location.endColumn)
      case None => (header.location.endLine, header.location.endColumn)
    }
    MdLocation(header.location.startLine, endLn, header.location.startColumn, endCol)
  }

  contents.foreach(_.setParent(Some(this)))

  override def getLabels: List[String] = header.mdString.string.trim +: altLabels

  private var _linkName: Option[String] = None

  def setLinkName(string: String) = _linkName = Some(string)

  def linkName = _linkName

  def isLinked = _linkName.isDefined

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

  override def equals(obj: scala.Any): Boolean = obj match {
    case ContentTag(otherContent, otherHeader, otherList, otherLocation, otherPath) =>
      contents.equals(otherContent) &&
      header.equals(otherHeader) &&
      altLabels.equals(otherList) &&
      location.equals(otherLocation) &&
      path.equals(otherPath)
    case _ => false
  }
}

object ContentTag extends ParseConstants {

  def apply(contents: List[Content], header: MdHeader, altLabels: List[String], path: String) = new ContentTag(
    contents, header, altLabels, path
  )

  def unapply(arg: ContentTag): Option[(List[Content], MdHeader, List[String], MdLocation, String)] = {
    Some(arg.contents, arg.header, arg.altLabels, arg.location, arg.path)
  }

}