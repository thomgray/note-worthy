package com.gray.note.content_things

import com.gray.markdown.produce.MdParser
import com.gray.markdown._
import com.gray.markdown.render.MdRenderer
import sun.security.util.PendingException

import scala.collection.mutable

trait ContentRenderer {

  def renderTag(contentTag: ContentTag, width: Int) = {
    val paragraphs = contentTag.contents filter {
      case tag: ContentTag if tag.isParentVisible => true
      case string: ContentString => true
      case _ => false
    } flatMap {
      case tag: ContentTag =>
        tag.header :: tag.get[ContentString].flatMap(_.paragraphs)
      case string: ContentString =>
        string.paragraphs
    }
    renderParagraphs(paragraphs, width)
  }

  def renderParagraphs(paragraphs: List[MdParagraph], width: Int) = {
    val doc = MdDocument(paragraphs)
    MdRenderer.render(doc, width).toString()
  }

//  def getLinksFromParagraph(paragraph: MdParagraph) = paragraph match {
//    List.empty[]
////    case string: MdString => string.links.map(_._1)
////    case _ => List.empty
//  }
//
//  def getLinksFromParagraphs(paragraphs: List[MdParagraph]) = {
//    var linkNumber = 1
//    paragraphs.flatMap {
//      case string: MdString =>
//        string.links.map{ link =>
//          link._1.index = linkNumber
//          linkNumber += 1
//          link._1
//        }
//      case list: MdList => List.empty
//      case _ => List.empty[MdLink]
//    }
//  }

}
