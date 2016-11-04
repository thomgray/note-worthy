package com.gray.note.ui

import com.gray.markdown.formatting.MdFormatter
import com.gray.note.Config
import com.gray.note.content_things.{ContentRenderer, ContentTag}
import com.gray.note.handling.ContentHandler
import com.gray.parse.{ParseConstants, ParseResult}

trait MainController {
  val terminal = Terminal
  val historian = SearchHistorian

  val handler = ContentHandler(Config.liveDirectories)

  implicit val renderer = new ContentRenderer() {}

  def mainLoop = {
    var continue = true
    while (continue){
      terminal.readLine.trim match {
        case "exit" | "quit" => continue = false; terminal.restore
        case ".." => if (historian.currentTag.isDefined && historian.currentTag.get.parentTag.isDefined) {
          println("DOING ..")
          val newTag = historian.currentTag.get.parentTag.get
          printTag(newTag)
        }
        case line if "^\\s*$".r.findFirstIn(line).isDefined =>
        case other =>
          getMergedResult(other) match {
            case Some(result) => printTag(result)
            case _ =>
          }
      }
    }
  }

  def getResults(string: String) ={
    historian.currentTag match {
      case Some(tag) =>
        val newQuery = tag.getQueryString + string
        handler.getContentWithQuery(newQuery, Some(tag)) match {
          case list if list.nonEmpty => list
          case _ => handler.getContentWithQuery(string)
        }
      case None => handler.getContentWithQuery(string)
    }
  }

  def getMergedResult(string: String) ={
    getResults(string) match {
      case list if list.nonEmpty => Some(handler.mergeResults(list))
      case _ => None
    }
  }

  def getAutocompletionOptions(string: String): List[String] = {
    historian.currentTag match {
      case Some(tag) => for {
        contentTag <- tag.getTagContents
        label = contentTag.getTitleString
        if label.startsWith(string)
      } yield label
      case _ => List.empty[String]
    }
  }

  def printTag(tag: ContentTag, popResult: Boolean = true) = {
    terminal.clear
    if (popResult) historian.popSearchResult(tag)
    println(TagRenderer.getHierarchyDiagram(tag))
    println(tag.getFormattedString(terminal.width))
  }

}

object MainController extends MainController
