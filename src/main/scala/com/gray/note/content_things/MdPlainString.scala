package com.gray.note.content_things

import com.gray.markdown.{MdLocation, MdParagraph}

case class MdPlainString(string: String, override val location: MdLocation) extends MdParagraph(location)

case class MdAlias(label: String, aliases: String, override val location: MdLocation) extends MdParagraph(location)