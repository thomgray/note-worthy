package com.gray.note.handling

import com.gray.note.content_things._
import com.gray.note.util.ResourceIO

trait SearchEngine {
  private[handling] val contentLoader: ContentLoader
  private[handling] val resourceIO: ResourceIO

  def getAllContentTags = for {
    baseTag <- getBaseTags
    tag <- baseTag.getAllNestedTags
    _ = if (tag.path == "") println(s"Tag without a file Path: ${tag.getTitleString}")
  } yield tag

  def getAllContentTagLikeThings = for {
    baseTag <- getBaseContent
    tag <- baseTag match {
      case ct: ContentTag => ct.getAllNestedTagLikeThings
      case ca: ContentTagAlias => List(ca)
    }
  } yield tag

  def getBaseTags = for {
    dir <- resourceIO.getDirectories
    baseContent <- contentLoader.getContentFromDirectory(dir)
    if baseContent.isInstanceOf[ContentTag]
  } yield baseContent.asInstanceOf[ContentTag]

  def getBaseContent = for {
    dir <- resourceIO.getDirectories
    baseContent <- contentLoader.getContentFromDirectory(dir)
    if baseContent.isInstanceOf[ContentTagLikeThing]
  } yield baseContent.asInstanceOf[ContentTagLikeThing]


  def getContentWithQuery(searchString: String, fromTag: Option[ContentTag] = None, allTagsOpt : Option[List[ContentTagLikeThing]] = None): List[ContentTag] = {
    val allTags = allTagsOpt getOrElse getAllContentTagLikeThings
    val matches = fromTag match {
      case None =>
        allTags.filter(tagMatchesSearchString(_, searchString))
      case Some(tag) => tag.getAllNestedTagLikeThings.filter(tagMatchesSearchString(_, searchString))
    }
    matches flatMap {
      case cta: ContentTagAlias =>
        getContentWithQuery(cta.getAliasedQuery, None, Some(allTags))
      case ct: ContentTag =>
        List(ct)
    }
  }

  def tagMatchesSearchString(tag: ContentTagLikeThing, searchString: String): Boolean = {
    def recTagMatchesString(tag: ContentTagLikeThing, searchString: String): Boolean = {
      val labels = tag.getLabels
      val remainders = checkLabelsWithString(labels, searchString)
      if (remainders.isEmpty) false
      else if (remainders.contains("") && (tag.isUniversallyReferenced || tag.parentTag.isEmpty)) {
        true
      }
      else if (tag.parentTag.isDefined) {
        val parent = tag.parentTag.get
        remainders.exists(recTagMatchesString(parent, _))
      }
      else false
    }
    val result = recTagMatchesString(tag, searchString)
    result
  }


  private def checkLabelsWithString(labels: List[String], searchString: String): List[String] = {
    val plainLabels = labels.filter(!_.matches("^\".*\"$")).map(_.toLowerCase)
    val regexLabels = labels.filter(_.matches("^\".*\"$")).map(_.stripPrefix("\"").stripSuffix("\"") + "$") //TODO case sensitive, so may want to make this insensitive, whilst preserving case of regex escapes

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

  def io = resourceIO

}

object SearchEngine {
  def apply(resourcePath: String): SearchEngine = new SearchEngine {
    override private[handling] val contentLoader: ContentLoader = SmartContentLoader
    override private[handling] val resourceIO: ResourceIO = new ResourceIO(resourcePath)
  }
}