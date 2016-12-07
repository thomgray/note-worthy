package com.gray.note.ui

import com.gray.note.content_things.ContentTag

import scala.collection.mutable

object SearchHistorian {

  private val searchResultBuffer = new mutable.ListBuffer[Option[ContentTag]]

  def popSearchResult(tag: ContentTag) = {
    searchResultBuffer.+=:(Some(tag))
    if (searchResultBuffer.length > 10) searchResultBuffer-= searchResultBuffer.last
  }

  def setCurrentTagToNone() = {
    searchResultBuffer.+=:(None)
    if (searchResultBuffer.length > 10) searchResultBuffer-= searchResultBuffer.last
  }

  def currentTag = if (searchResultBuffer.isDefinedAt(0)) searchResultBuffer(0) else None
}
