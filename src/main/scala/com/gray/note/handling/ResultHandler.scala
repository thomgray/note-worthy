package com.gray.note.handling

import com.gray.markdown._
import com.gray.markdown.parsing.MdParser
import com.gray.note.content_things.{ContentString, ContentTag}
import sys.process._

trait ResultHandler {
  var currentTagURLS = List.empty[MdLink]
  var currentParagraphs = List.empty[MdParagraph]

  def apply(contentTag: ContentTag) = {
    currentParagraphs = paragraphsForTag(contentTag)
    currentTagURLS = gatherLinks(currentParagraphs)
  }

  def paragraphsForTag(contentTag: ContentTag) = contentTag.getContents filter {
    case tag: ContentTag if tag.isParentVisible => true
    case string: ContentString => true
    case _ => false
  } flatMap {
    case tag: ContentTag => MdHeader(tag.getTitleString, 5) :: tag.get[ContentString].flatMap(getMdContentsFromContentString)
    case string: ContentString => getMdContentsFromContentString(string)
  }

  private def getMdContentsFromContentString(string: ContentString): List[MdParagraph] = {
    string.format match {
      case "txt" => List(MdPlainString(string.getString))
      case "md" => MdParser.parse(string.getString)
    }
  }

  def gatherLinks(mdParagraphs: List[MdParagraph]) = {
    val links = mdParagraphs.flatMap(gatherLinksForParagraph)
    var linkNumber = 1
    links.foreach { l => l.index = linkNumber; linkNumber += 1 }
    links
  }

  private def gatherLinksForParagraph(mdParagraph: MdParagraph): List[MdLink] = mdParagraph match {
    case string: MdString => string.links.map(_._1)
    case list: MdList =>
      list.items.flatMap(_.items.flatMap(gatherLinksForParagraph))
    case _ => List.empty
  }

  def openURL(string: String) = {
    if (string.forall(_.isDigit) && string.toInt < currentTagURLS.length) {
      val index = string.toInt
      currentTagURLS(index).open
    } else {
      if (string.forall(_.isDigit)) println(s"tried to open an url [$string] but there are not enough links!")
      currentTagURLS.find(l => l.inlineString.getOrElse(l.url) == string) match {
        case Some(link) =>
          link.open
        case None =>
      }
    }
  }

  def openTagInAtom(tag: ContentTag) = {
    val location = s"${tag.location.lineStart+1}:${tag.location.columnStart+1}"
    println(s"Opening ${tag.filePath}:$location")
    s"atom ${tag.filePath}:$location".!
  }

  def getNextSiblingTag(contentTag: ContentTag) = contentTag.parentTag match {
    case Some(parent) =>
      val visibleChildren = parent.getTagContents.filter(_.isContentVisible)
      visibleChildren.indexOf(contentTag) match {
        case i if i >= 0 && i < visibleChildren.length-1 => Some(visibleChildren(i+1))
        case _ => None
      }
    case _ => None
  }

  def getPreviousSiblingTag(contentTag: ContentTag) = contentTag.parentTag match {
    case Some(parent) =>
      val visibleChildren = parent.getTagContents.filter(_.isContentVisible)
      visibleChildren.indexOf(contentTag) match {
        case i if i > 0  => Some(visibleChildren(i-1))
        case _ => None
      }
    case _ => None
  }


}

object ResultHandler extends ResultHandler
