package com.gray.note.content_things

import com.gray.markdown.{MdLocation, MdParagraph}
import com.gray.parse.ParseConstants

class ContentString(val paragraphs: List[MdParagraph],
                    val format: String,
                    override val path: String = "") extends Content {

  override val location: MdLocation = MdLocation(
    paragraphs.head.location.startLine,
    paragraphs.last.location.endColumn,
    paragraphs.head.location.startColumn,
    paragraphs.last.location.endColumn
  )

  override def getString: String = paragraphs.map(_.toString).mkString("\n")

  override def equals(obj: scala.Any): Boolean = obj match {
    case ContentString(otherPars, otherFormat, otherLocation, otherPath) =>
      paragraphs.equals(otherPars) &&
      format.equals(otherFormat) &&
      location.equals(otherLocation) &&
      path.equals(otherPath)
    case _ => false
  }

}

object ContentString extends ParseConstants {

  def apply(paragraphs: List[MdParagraph], format: String, path: String = "") = new ContentString(
    paragraphs, format, path
  )

  def unapply(cs: ContentString) = {
    Some(cs.paragraphs, cs.format, cs.location, cs.path)
  }

}