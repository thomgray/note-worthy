package com.gray.note.ui

import com.gray.note.content_things.ContentTag

import scala.io.AnsiColor

object TagRenderer extends AnsiColor{
  private val tab = "    "

  def getTagString(tag: ContentTag, width: Int = 0) = {
    tag.getString
  }

  def getContentDiagram(tag: ContentTag) = {
    var str = "── "+BOLD+tag.getTitleString+RESET
    val contents = tag.getTagContents
    for (i <- contents.indices; tg = contents(i)){
      val line = if(i==contents.length-1) "└─ " else "├─ "
      str = str+tab+line+tg.getTitleString
    }
  }

  def getHierarchyDiagram(tag: ContentTag) = {
    val linearHierarchy = getLinearHierarchy(tag)
    var str = ""
    var indent = ""
    for (tg <- linearHierarchy){
      val titleString = if (tg==tag) BOLD+tg.getTitleString+RESET else tg.getTitleString
      if (tg.parentTag.isEmpty) str = str + s"\n$indent── " + titleString
      else str = str + s"\n$indent└─ " + titleString
      indent += tab
    }
    if (tag.getContents.nonEmpty) {
      val contents = tag.getTagContents
      for (i <- contents.indices; tg = contents(i)) {
        if (i == contents.length-1) str = str + s"\n$indent└─ " + tg.getTitleString
        else str = str + s"\n$indent├─ " + tg.getTitleString
      }
    }
    str.substring(1)
  }

  private def getLinearHierarchy(tag: ContentTag) = {
    var listout = List[ContentTag](tag)
    var _tag = tag
    while (_tag.parentTag.isDefined){
      val parent = _tag.parentTag.get
      listout = parent +: listout
      _tag = parent
    }
    listout
  }

}
