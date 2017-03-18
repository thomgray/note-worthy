package com.gray.note.ui

import com.gray.note.content_things.{Content, ContentTag}
import com.gray.note.util.Formatting

import scala.io.AnsiColor

object TagRenderer extends AnsiColor with Formatting {
  private val tab = "    "

  def getTagString(tag: ContentTag, width: Int = 0) = {
    tag.getString
  }

  def getHierarchyDiagram(tag: ContentTag) = {
    val linearHierarchy = tag.getLinearHierarchy
    var str = ""
    var indent = ""

    for (tg <- linearHierarchy.init) {
      if (tg.parentTag.isEmpty) str += s"\n$indent── " + tg.getTitleString
      else str = str + s"\n$indent└─ " + tg.getTitleString
      indent += tab
    }

    if (linearHierarchy.length == 1) { // tag is the only member
      str += s"\n$indent── " + BOLD + tag.getTitleString + RESET
      str += getContentsDiagramOfTag(false)
    }
    else {
      // there is a parent
      val parent = tag.parentTag.get
      val parentContents = parent.getTagContents.filter(_.isContentVisible)
      for (i <- parentContents.indices; tg = parentContents(i)) {
        val isTag = tg == tag
        val titleString = if (isTag) BOLD + tg.getTitleString + RESET else tg.getTitleString
        val link = if (i == parentContents.length - 1) "└─ " else "├─ "
        str += "\n" + indent + link + titleString
        if (isTag) str += getContentsDiagramOfTag(i < parentContents.length - 1)
      }
    }

    def getContentsDiagramOfTag(drawLine: Boolean) = {
      val line = if (drawLine) "│" else " "
      val secondTab = tab.substring(1)
      var _str = ""

      val contents = tag.getTagContents.filter(_.isContentVisible)
      for (i <- contents.indices; tg = contents(i)) {
        val link = if (i == contents.length-1) "└─ " else "├─ "
        _str += "\n" + indent + line + secondTab + link + tg.getTitleString
      }
      _str
    }


//    for (tg <- linearHierarchy){
//      val titleString = if (tg==tag) BOLD+tg.getTitleString+RESET else tg.getTitleString
//      if (tg.parentTag.isEmpty) str = str + s"\n$indent── " + titleString
//      else str = str + s"\n$indent└─ " + titleString
//      indent += tab
//    }
//    if (tag.getContents.nonEmpty) {
//      val contents = tag.getTagContents.filter(_.isContentVisible)
//      for (i <- contents.indices; tg = contents(i)) {
//        if (i == contents.length-1) str = str + s"\n$indent└─ " + tg.getTitleString
//        else str = str + s"\n$indent├─ " + tg.getTitleString
//      }
//    }
    str.substring(1)
  }


}
