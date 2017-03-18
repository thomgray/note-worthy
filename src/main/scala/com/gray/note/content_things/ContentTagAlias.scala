package com.gray.note.content_things

import com.gray.markdown.MdLocation
import com.gray.parse.ParseConstants

class ContentTagAlias( val alias: String,
                       override val labels: List[String],
                       override val location: MdLocation,
                       override val path: String = "") extends ContentTagLikeThing{

  override def isParaphrase: Boolean = true

  override def getString: String = alias

  def getAliasedQuery = parentTag match {
    case Some(parent) => s"${parent.getQueryString} $getString"
    case None => getString
  }

  override def equals(obj: scala.Any): Boolean = obj match {
    case ContentTagAlias(otherAlias, otherLabels, otherLocation, otherPath) =>
      alias.equals(otherAlias) &&
      labels.equals(otherLabels) &&
      location.equals(otherLocation) &&
      path.equals(otherPath)
    case _ => false
  }
}

object ContentTagAlias extends ParseConstants {

  def apply(alias: String, labels: List[String], location: MdLocation, path: String) = new ContentTagAlias(
    alias, labels, location, path
  )

  def apply(mdAlias: MdAlias, path: String) = new ContentTagAlias(
    mdAlias.aliasLabels, mdAlias.aliasedLabel.split(";").map(_.trim.toLowerCase).toList, mdAlias.location, path
  )

  def unapply(arg: ContentTagAlias): Option[(String, Seq[String], MdLocation, String)] =
    Some(arg.alias, arg.labels, arg.location, arg.path)
}