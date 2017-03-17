package com.gray.note.content_things

import java.io.File

import com.gray.markdown.@@
import com.gray.markdown.util.MapOne
import com.gray.parse._
import com.gray.parse.mdlparse.MdlIterator
import com.gray.parse.mdparse.MdIterator

object SmartContentLoader extends ContentLoader with MapOne {

  def getContent(string: String, path: String, parser: ContentParser, offset: Int): List[Content] = {
    val mdParagraphs = parser(string)
    translateResultToContent(mdParagraphs, path)
  }

  private def translateResultToContent(list: List[AbstractParseResult], path: String): List[Content] = {
    list map {
      case StringParseResult(pars) =>
        new ContentString(
          pars,
          "",
          @@(pars.head.location.startLine, pars.last.location.endLine),
          path
        )
      case TagParseResult(pars, header, altLabels) =>
        val contents = translateResultToContent(pars, path)
        new ContentTag(
          contents,
          header,
          altLabels,
          @@(contents.head.location.startLine, contents.last.location.endLine),
          path
        )
    }
  }

  override def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    val parser = decideOnParser(string, extn)
    getContent(string, path, parser)
  }

  override def getContentFromDirectory(path: String): List[Content] = {
    val paths = new File(path).listFiles.filter({ f =>
      f.getName.endsWith(".txt") || f.getName.endsWith(".md")
    }).toList.map(_.toString)
    paths.flatMap(path => getContentFromFile(path))
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
