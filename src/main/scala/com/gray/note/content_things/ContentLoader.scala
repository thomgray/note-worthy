package com.gray.note.content_things

import java.io.File

import com.gray.parse.{MdlParser, ParseConstants, ParseResult, Parser}


/**
  * Dedicated to loading content from a string, file or directory.<br/>
  * Requires implementation of parser() which must return a Parser<br/>
  * <dl>
  *   <dt>MdlLoader</dt>
  *   <dd>uses an MdlParser</dd>
  * </dl>
  */
trait ContentLoader extends ParseConstants {
  private[content_things] def parser(string: String): Parser

  def getContent(string: String): List[Content] = {
    parser(string).parseForResults map {
      case result@ParseResult(string, _, CONTENT_TAG, _) =>
        val tag = new ContentTag(result)
        val tagContents = getContent(string)
        tag.setContents(tagContents)
        tag.getContents.foreach(t => t.setParent(Some(tag)))
        tag
      case result@ParseResult(_, _, CONTENT_ALIAS, _) =>
        new ContentTagAlias(result)
      case result@ParseResult(str, _, CONTENT_STRING, _) =>
        new ContentString(str)
    }
  }

  def getContentFromFile(path: String) = {
    val extn = "\\w{2,3}$".r.findFirstIn(path).getOrElse("txt")
    val string = io.Source.fromFile(path).mkString.replace("\t", "    ")
    val content = getContent(string)
    for {
      content0 <- content
      content1 <- content0.getAllDescendantContent
      if content1.isInstanceOf[ContentString]
      _ = content1.asInstanceOf[ContentString].setFormat(extn)
    } yield ()
    content match {
      case list if !list.exists(_.isInstanceOf[ContentTag]) =>
        getTagFromUnTaggedFile(path, string, list)
      case other => other
    }
  }

  private def getTagFromUnTaggedFile(filePath: String, string: String, list: List[Content]) = {
    """\.\w{2,3}$""".r.findFirstIn(filePath) match {
      case Some(extn) =>
        val filename = filePath.stripSuffix(extn).split("/").last
        val labels = Some(filename.split(";").map(_.trim).toList)
        val result = ParseResult(string, labels, CONTENT_TAG, "")
        val tag = new ContentTag(result)
        tag.setContents(list)
        List(tag)
      case None => list
    }
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
      val newContent = new ContentTag(dummyResult)
      val contents = list flatMap (_.getContents)
      newContent.setContents(contents)
      newContent
    }
  }

}

object MdlLoader extends ContentLoader {
  override private[content_things] def parser(string: String): Parser = MdlParser(string)
}