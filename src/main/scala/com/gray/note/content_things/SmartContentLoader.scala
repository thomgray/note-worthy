package com.gray.note.content_things

import com.gray.parse.ContentParser
import com.gray.parse.mdlparse.MdlIterator
import com.gray.parse.mdparse.MdIterator

object SmartContentLoader extends ContentLoader {

  override def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    val parser = decideOnParser(string, extn)

    val content = getContent(string, path, parser)
    for {
      content0 <- content
      content1 <- content0.getAllDescendantContent
      if content1.isInstanceOf[ContentString]
      _ = content1.asInstanceOf[ContentString].setFormat(extn)
    } yield ()
    content match {
      case other => other
    }
  }

  private def decideOnParser(string: String, extension: String) = {
    if ("^\\s*\\[\\[\\[".r.findFirstIn(string).isDefined) {
      MdlIterator
    }else if (extension=="md") {
      MdIterator
    } else {
      throw new Exception(s"cannot suggest a parser for file with extention $extension with content \n$string")
    }
  }

  override private[content_things] def getParser: ContentParser = null
}
