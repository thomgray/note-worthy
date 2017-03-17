package com.gray.note.content_things

import java.io.File

import com.gray.markdown.@@
import com.gray.parse._
import com.gray.parse.mdlparse.MdlIterator
import com.gray.parse.mdparse.MdIterator


/**
  * Dedicated to loading content from a string, file or directory.<br/>
  * Requires implementation of parser() which must return a Parser<br/>
  * <dl>
  *   <dt>MdlLoader</dt>
  *   <dd>uses an MdlParser</dd>
  * </dl>
  */
trait ContentLoader extends ParseConstants {
  private[content_things] def getParser: ContentParser

  def getContent(string: String, path: String = "", parser: ContentParser = getParser): List[Content] = {
    val mdParagraphs = parser(string)
    translateResultsToContent(mdParagraphs, path)
  }

  def translateResultsToContent(results: List[AbstractParseResult], path: String): List[Content] = {
    results map {
      case StringParseResult(pars) =>
        new ContentString(
          pars,
          "",
          @@(pars.head.location.startLine, pars.last.location.endLine),
          path
        )
      case TagParseResult(pars, header, altLabels) =>
        val contents = translateResultsToContent(pars, path)
        new ContentTag(
          contents,
          header,
          altLabels,
          @@(header.location.startLine, contents.lastOption.map(_.location).getOrElse(header.location).endLine),
          path
        )
    }
  }

  def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    getContent(string, path, getParser)
  }

  def getContentFromDirectory(path: String) = {
    val paths = new File(path).listFiles.filter({ f =>
      f.getName.endsWith(".txt") || f.getName.endsWith(".md")
    }).toList.map(_.toString)
    paths.flatMap( path => getContentFromFile(path))
  }

  def mergeContentTags(list: List[ContentTag]) = {
    if (list.length==1) list.head
    else{
      val dummyResult = ParseResult("",
        Some(List(list.head.getTitleString)),
        CONTENT_TAG,
        ""
      )
      new ContentTag(
        list.flatMap(_.contents),
        list.head.header,
        list.flatMap(_.altLabels),
        list.head.location,
        list.head.path
      )
    }
  }

}

object MdlLoader extends ContentLoader {
  override private[content_things] def getParser: ContentParser = MdlIterator
}

object MdLoader extends ContentLoader {
  override private[content_things] def getParser: ContentParser = MdIterator
}