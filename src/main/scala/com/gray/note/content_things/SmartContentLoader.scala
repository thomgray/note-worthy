package com.gray.note.content_things

import com.gray.markdown.util.MapOne
import com.gray.parse._
import com.gray.parse.mdlparse.MdlIterator
import com.gray.parse.mdparse.MdIterator

object SmartContentLoader extends ContentLoader with MapOne {

  override def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    val parser = decideOnParser(string, extn)
    getContent(string, path, parser, extn)
  }

  private def decideOnParser(string: String, extension: String) = {
    if ("^\\s*\\[\\[\\[".r.findFirstIn(string).isDefined) {
      MdlIterator
    } else if (extension == "md") {
      MdIterator
    } else {
      MdlIterator
    }
  }

  override private[content_things] def getParser: ContentParser = null
}
