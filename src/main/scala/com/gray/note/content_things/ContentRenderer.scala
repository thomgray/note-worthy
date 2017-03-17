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
        MdHeader(MdString(tag.getTitleString, @@(0,0)), 5, @@(0,0)) :: tag.get[ContentString].flatMap(getMdContentsFromContentString)
      case string: ContentString =>
        getMdContentsFromContentString(string)
    }
    renderParagraphs(paragraphs, width)
  }

  private def getMdContentsFromContentString(string: ContentString) = {
    string.format match {
      case "txt" => List(MdPlainString(string.getString, @@(0,0)))
      case "md" =>
        MdParser.parse(string.getString).paragraphs
      case other => throw new PendingException(s"awaiting for tag: $string")
    }
  }

  def renderParagraphs(paragraphs: List[MdParagraph], width: Int) = {
    val doc = MdDocument(paragraphs)
    MdRenderer.render(doc, width).toString()
//    var linkNumber = 1
//    var checkboxNumber = 1
//    paragraphs.foreach {
//      case MdPlainString(string) => mdFormatter.formatString(string, width)
//      case string: MdString =>
//        string.links.foreach { s => s._1.index = linkNumber; linkNumber += 1 }
//      case list: MdCheckList =>
//        list.items.foreach { item => item.index = checkboxNumber; checkboxNumber += 1 }
//      case _ =>
//    }
//
//    paragraphs.map(mdFormatter.renderParagraph(_, width)).mkString("\n\n")
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
