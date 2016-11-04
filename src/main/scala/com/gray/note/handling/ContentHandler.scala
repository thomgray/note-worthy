package com.gray.note.handling

import com.gray.note.content_things.{ContentLoader, ContentTag, MdlLoader}
import com.gray.parse.{ParseConstants, ParseResult}
import com.gray.util.ResourceIO

trait ContentHandler {
  private[handling] val contentLoader: ContentLoader
  private[handling] val resourceIO: ResourceIO

  def getAllContentTags = for {
    dir <- resourceIO.getDirectories
    baseContent <- contentLoader.getContentFromDirectory(dir)
    if baseContent.isInstanceOf[ContentTag]
    tag <- baseContent.asInstanceOf[ContentTag].getAllNestedTags
  } yield tag

  def getContentWithQuery(searchString: String, fromTag: Option[ContentTag] = None) = {
    fromTag match {
      case None => getAllContentTags.filter(tagMatchesSearchString(_,searchString))
      case Some(tag) => tag.getAllNestedTags.filter(tagMatchesSearchString(_,searchString))
    }
  }

  def tagMatchesSearchString(tag: ContentTag, searchString: String): Boolean = {
    val labels = tag.getLabels
    val remainders = checkLabelsWithString(labels, searchString)
    if (remainders.isEmpty) false
    else if (remainders.contains("") && (tag.isUniversallyReferenced || tag.parentTag.isEmpty)) true
    else if (tag.parentTag.isDefined){
      val parent = tag.parentTag.get
      remainders.exists(tagMatchesSearchString(parent, _))
    }
    else false
  }


  private def checkLabelsWithString(labels: List[String], searchString: String): List[String] = {
    val plainLabels = labels.filter(!_.matches("^\".*\"$")).map(_.toLowerCase)
    val regexLabels = labels.filter(_.matches("^\".*\"$")).map(_.stripPrefix("\"").stripSuffix("\"")+"$") //TODO case sensitive, so may want to make this insensitive, whilst preserving case of regex escapes

    val plainMatches = for {
      label <- plainLabels
      if searchString.endsWith(label)
    } yield searchString.stripSuffix(label).trim

    val regexMatches = for {
      regexString <- regexLabels
      regex = regexString.r
      matchString <- regex.findFirstIn(searchString)
    } yield searchString.stripSuffix(matchString).trim

    plainMatches ++ regexMatches
  }

  def mergeResults(list: List[ContentTag]) = contentLoader.mergeContentTags(list)

}

object ContentHandler{
  def apply(resourcePath: String): ContentHandler = new ContentHandler{
    override private[handling] val contentLoader: ContentLoader = MdlLoader
    override private[handling] val resourceIO: ResourceIO = new ResourceIO(resourcePath)
  }
}