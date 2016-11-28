package com.gray.note.content_things

import com.gray.markdown._
import com.gray.markdown.formatting.MdFormatter
import com.gray.markdown.parsing.MdParser

import scala.collection.mutable

trait ContentRenderer {
  val mdFormatter = MdFormatter()

//  def renderContent(content: Content, width: Int) = {
//    content match {
//      case string: ContentString =>
//        renderContentString(string, width)
//      case tag: ContentTag => renderContentTag(tag, width)
//    }
//  }

//  private def renderContentString(contentString: ContentString, width: Int) = contentString.format match {
//    case "txt" => renderStringAsTxt(contentString, width)
//    case "md" => renderStringAsMd(contentString, width)
//  }

//  private def parseContent(content: Content) = content match {
//    case string: ContentString =>
//      string.setParagraphs(MdParser.parse(string.getString))
//    case tag: ContentTag =>
//      tag.get[ContentString].foreach(s => s.setParagraphs(MdParser.parse(s.getString)))
//  }
//
//  private def renderStringAsTxt(contentString: ContentString, width: Int) = {
//    MdFormatter().formatString(contentString.getString, width)
//  }
//
//  private def renderStringAsMd(contentString: ContentString, width: Int) = {
//    val mdParagraphs = MdParser.parse(contentString.getString)
//    contentString.setParagraphs(mdParagraphs)
//
//
//    val strings = MdParser.parse(contentString.getString) map (mdFormatter.renderParagraph(_, width))
//    strings.mkString("\n\n")
//  }

//  private def renderContentTag(contentTag: ContentTag, width: Int): String = {
//    (contentTag.getContents.filter {
//      case string: ContentString => true
//      case tag: ContentTag if tag.isParentVisible => true
//      case _ => false
//    } map (renderContent(_, width))).mkString("\n") + "\n"
//  }

  def renderTag(contentTag: ContentTag, width: Int) = {
    var linkRefs = List.empty[MdLinkRef]
    val paragraphs = contentTag.getContents filter {
      case tag: ContentTag if tag.isParentVisible => true
      case string: ContentString => true
      case _ => false
    } flatMap {
      case tag: ContentTag => MdHeader(tag.getTitleString, 5) :: tag.get[ContentString].flatMap(getMdContentsFromContentString)
      case string: ContentString => getMdContentsFromContentString(string)
    }
    renderParagraphs(paragraphs, width)
  }

  private def getMdContentsFromContentString(string: ContentString) = {
    string.format match {
      case "txt" => List(MdPlainString(string.getString))
      case "md" => MdParser.parse(string.getString)
    }
  }

  def renderParagraphs(paragraphs: List[MdParagraph], width: Int) = {
    var linkNumber = 1
    var checkboxNumber = 1
    paragraphs.foreach {
      case MdPlainString(string) => mdFormatter.formatString(string, width)
      case string: MdString =>
        string.links.foreach{s => s._1.index = linkNumber; linkNumber += 1}
      case list: MdCheckList =>
        list.items.foreach{item => item.index = checkboxNumber; checkboxNumber += 1}
      case _ =>
    }

    paragraphs.map(mdFormatter.renderParagraph(_, width)).mkString("\n\n")
  }

  def getLinksFromParagraph(paragraph: MdParagraph) = paragraph match {
    case string: MdString => string.links.map(_._1)
    case _ => List.empty
  }

//  private def renderTagAsMd(contentTag: ContentTag, width: Int) = {
//    val visibleContent = contentTag.getContents.filter {
//      case string: ContentString =>
//        string.setParagraphs(MdParser.parse(string.getString))
//        true
//      case tag: ContentTag if tag.isParentVisible =>
//        tag.get[ContentString].foreach(s => MdParser.parse(s.getString))
//        true
//      case _ => false
//    }
//
//    val paragraphs = visibleContent.flatMap {
//      case string: ContentString => string.paragraphs().getOrElse(List.empty[MdParagraph])
//      case tag: ContentTag => tag.get[ContentString].flatMap(_.paragraphs().getOrElse(List.empty[MdParagraph]))
//    }
//
//  }
}
