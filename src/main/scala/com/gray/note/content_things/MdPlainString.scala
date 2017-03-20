package com.gray.note.content_things

import com.gray.markdown.produce.parsingrules.MdParsingRule
import com.gray.markdown.{@@, MdDocument, MdLocation, MdParagraph}

case class MdPlainString(string: String, override val location: MdLocation) extends MdParagraph(location)

case class MdAlias(aliasedLabel: String, aliasLabels: String, override val location: MdLocation) extends MdParagraph(location)


object MdAliasParsingRule extends MdParsingRule {
  val aliasRegex = """^\s*\[(.+)\]\s*<(.*)>\s*$""".r

  override def findParagraph(lines: List[String], marker: Int, offset: Int, parser: (List[String], Int) => MdDocument): Option[(MdParagraph, Int)] = aliasRegex.findFirstMatchIn(lines(marker)) map { mtch =>
    (MdAlias(mtch.group(2), mtch.group(1), @@(0,0)), marker+1)
  }
}