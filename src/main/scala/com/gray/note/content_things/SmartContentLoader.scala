package com.gray.note.content_things

import java.io.File

import com.gray.parse.mdlparse.MdlIterator
import com.gray.parse.mdparse.MdIterator
import com.gray.parse.{ContentParser, ParseConstants, ParseIterator, ParseResult}

object SmartContentLoader extends ContentLoader {

  private def getContentWithParser(string: String, path: String = "", parser: ContentParser, offset : Int = 0): List[Content] = {
    parser(string) map {
      case result@ParseResult(string, _, CONTENT_TAG, _,_) =>
        val tag = new ContentTag(result, path)
        val tagContents = getContentWithParser(string, path, parser, offset + result.location.lineStart+1)
        tag.setContents(tagContents)
        tag.getContents.foreach(t => t.setParent(Some(tag)))
        tag
      case result@ParseResult(_, _, CONTENT_ALIAS, _, _) =>
        new ContentTagAlias(result)
      case result@ParseResult(str, _, CONTENT_STRING, _, _) =>
        new ContentString(str)
    }
  }

  override def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    val parser = decideOnParser(string, extn)

    val content = getContentWithParser(string, path, parser)
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

  private def getTagFromUnTaggedFile(filePath: String, string: String, list: List[Content]) = {
    """\.\w{2,3}$""".r.findFirstIn(filePath) match {
      case Some(extn) =>
        val filename = filePath.stripSuffix(extn).split("/").last
        val labels = Some(filename.split(";").map(_.trim).toList)
        val result = ParseResult(string, labels, CONTENT_TAG, "")
        val tag = new ContentTag(result, filePath)
        tag.setContents(list)
        List(tag)
      case None => list
    }
  }

  override private[content_things] def parser: ContentParser = null
}
