package com.gray.note.ui

import com.gray.note.content_things.ContentTag

import scala.collection.mutable

object SearchHistorian {

  val searchResultBuffer = new mutable.MutableList[ContentTag]

  def popSearchResult(tag: ContentTag) = {
    searchResultBuffer.+=:(tag)
  }

  def currentTag = if (searchResultBuffer.isDefinedAt(0)) Some(searchResultBuffer(0)) else None
}
