package com.gray.note.content_things

import java.io.File

import com.gray.markdown.MdParagraph
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

  def getContent(string: String, path: String = "", parser: ContentParser = getParser, extension: String): List[Content] = {
    val mdParagraphs = parser(string, extension)
    val asContent = translateResultsToContent(mdParagraphs, path)
    extractContentAliasesFromStrings(asContent)
  }

  protected def translateResultsToContent(results: List[AbstractParseResult], path: String): List[Content] = {
    results map {
      case StringParseResult(pars) =>
        new ContentString(
          pars,
          "",
          path
        )
      case TagParseResult(pars, header, altLabels) =>
        val contents = translateResultsToContent(pars, path)
        new ContentTag(
          contents,
          header,
          altLabels,
          path
        )
    }
  }

  def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    getContent(string, path, getParser, extn)
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
      new ContentTag(
        list.flatMap(_.contents),
        list.head.header,
        list.flatMap(_.altLabels),
        list.head.path
      )
    }
  }

  def extractContentAliasesFromStrings(list: List[Content]): List[Content] = {
    list flatMap{
      case ContentTag(content, header, labels, location, path) =>
        val newTagContents = extractContentAliasesFromStrings(content)
        val tag = new ContentTag(newTagContents, header, labels, path)
        List(tag)
      case ContentString(pars, format, location, path) =>
        val split = separateMdParagrapsIntoAliasesAndTheRest(pars).filter(_.nonEmpty)
        split.map{
          case List(MdAlias(alias, label, loc)) =>
            val labels = label.split(";").map(_.trim.toLowerCase).toList
            new ContentTagAlias(alias, labels, loc, path)
          case paragraphs =>
            new ContentString(paragraphs, format, path)
        }
    }
  }

  private def separateMdParagrapsIntoAliasesAndTheRest(pars: List[MdParagraph], soFar: List[List[MdParagraph]] = Nil): List[List[MdParagraph]] = {
    pars.indexWhere(_.isInstanceOf[MdAlias]) match {
      case -1 => soFar :+ pars
      case other =>
        val untilThat = pars.take(other) match {
          case Nil => soFar
          case list => soFar :+ list
        }
        val that = List(pars(other))
        val remainder = pars.drop(other+1)
        separateMdParagrapsIntoAliasesAndTheRest(remainder, untilThat :+ that)
    }
  }

}

object MdlLoader extends ContentLoader {
  override private[content_things] def getParser: ContentParser = MdlIterator
}

object MdLoader extends ContentLoader {
  override private[content_things] def getParser: ContentParser = MdIterator
}