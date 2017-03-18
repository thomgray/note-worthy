package com.gray.note.handling

import com.gray.markdown._
import com.gray.markdown.produce.MdParser
import com.gray.note.content_things.{ContentString, ContentTag}
import sun.security.util.PendingException

import sys.process._

trait ResultHandler {
  var currentTagURLS = List.empty[MdLink]
  var currentParagraphs = List.empty[MdParagraph]
  var currentTag: Option[ContentTag] = None

  var contentTags = List.empty[ContentTag]

  def apply(contentTag: ContentTag) = {
    currentTag = Some(contentTag)
    currentParagraphs = paragraphsForTag(contentTag)
    currentTagURLS = gatherLinks(currentParagraphs)
  }

  def paragraphsForTag(contentTag: ContentTag) = contentTag.contents filter {
    case tag: ContentTag if tag.isParentVisible => true
    case string: ContentString => true
    case _ => false
  } flatMap {
    case tag: ContentTag =>
      tag.header :: tag.get[ContentString].flatMap(_.paragraphs)
    case string: ContentString =>
      string.paragraphs
    case other => throw new PendingException(s"need to apply match for tag: $other")
  }

  def gatherLinks(mdParagraphs: List[MdParagraph]) = {
    mdParagraphs.flatMap(gatherLinksForParagraph)
  }

  private def gatherLinksForParagraph(mdParagraph: MdParagraph): List[MdLink] = mdParagraph match {
    case string: MdString =>
      string.links()
    case list: MdList[MdListItem] =>
      list.items.flatMap(_.paragraphs).collect({
        case linkable: MdLinkable => linkable
      }).flatMap(gatherLinksForParagraph)
    case _ => List.empty
  }

  def openURL(string: String) = {
    currentTagURLS.find(url => url.label.map(_.equals(string)).isDefined) match {
      case Some(url) =>
        openActualUrl(url.url)
      case None => openActualUrl(string)
    }
  }

  private def openActualUrl(string: String) = {
    if (string.startsWith("http")){
      s"open $string"!
    }  else s"open https://$string".!
  }

  def openTagInAtom(tag: ContentTag) = {
    val location = s"${tag.location.startLine+1}:${tag.location.startColumn+1}"
    println(s"Opening ${tag.path}:$location")
    s"atom ${tag.path}:$location".!
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
