package com.gray.note.content_things

import com.gray.markdown.formatting.MdFormatter
import com.gray.markdown.parsing.MdParser

trait ContentRenderer {
  val mdFormatter = MdFormatter

  def renderContent(content: Content, width: Int) = {
    content match {
      case string: ContentString =>
        renderContentString(string, width)
      case tag: ContentTag => renderContentTag(tag, width)
    }
  }

  private def renderContentString(contentString: ContentString, width: Int) = contentString.format match {
    case "txt" => renderStringAsTxt(contentString, width)
    case "md" => renderStringAsMd(contentString, width)
  }


  private def renderStringAsTxt(contentString: ContentString, width: Int) = {
    contentString.getString
  }

  private def renderStringAsMd(contentString: ContentString, width: Int) = {
    val strings = MdParser.parse(contentString.getString) map (mdFormatter.renderParagraph(_, width))
    strings.mkString("\n\n")
  }

  private def renderContentTag(contentTag: ContentTag, width: Int): String = {
    (contentTag.getContents.filter {
      case tag: ContentTag if tag.isParentVisible => true
      case _ => true
    } map (renderContent(_, width))).mkString("\n")
  }

}
