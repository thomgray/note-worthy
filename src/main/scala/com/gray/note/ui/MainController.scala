package com.gray.note.ui

import com.gray.note.Config
import com.gray.note.Config._
import com.gray.note.content_things.{ContentRenderer, ContentTag}
import com.gray.note.handling.{ResultHandler, SearchEngine}
import com.gray.util.WordIterator

import scala.collection.mutable

object MainController extends ArgKeys {
  val terminal = Terminal
  val historian = SearchHistorian

  val resultHandler = ResultHandler
  val searchEngine = SearchEngine(Config.liveRootDirectory)

  implicit val renderer = new ContentRenderer() {}

  def specialCommand: Unit = {
    Terminal.special
  }

  def mainLoop {
    var continue = true
    while (continue) {
      terminal.readLine.trim match {
        case "exit" | "quit" => continue = false; terminal.restore
        case str if str.startsWith(urlOpenCommand) => handleOpen(str.substring(1).trim)
        case str if str.startsWith(resetCurrentTagCommand) => handleReset(str.substring(1).trim)
        case str if str.startsWith(".") => handleDotCommand(str)
        case str if str.startsWith(":") => handleMeta(str.substring(1).trim)
        case "" =>
        case str => handleQuery(str)
      }
    }
  }

  def handleOpen(string: String) = string match {
    case str if str == "+" && historian.currentTag.isDefined =>
      resultHandler.openTagInAtom(historian.currentTag.get)
    case str if historian.currentTag.isDefined =>
      resultHandler.openURL(str)
    case _ =>
  }

  def handleMeta(string: String) = string match {
    case "l" | "list" =>
      searchEngine.io.getDirectories.foreach(println)
    case str if str.startsWith("+") =>
      val newFile = str.stripPrefix("+").trim
      searchEngine.io.addDirectory(newFile)
    case str if str.startsWith("-") =>
      val remove = str.stripPrefix("-").trim
      searchEngine.io.removeDirectory(remove)
    case _ =>
  }

  def handleQuery(string: String) = regularSearch(string)

  def handleReset(string: String) = {
    historian.setCurrentTagToNone()
    string match {
      case "" => terminal.clear
      case _ => regularSearch(string)
    }
  }

  def handleDotCommand(string: String) = string match {
    case ".." if historian.currentTag.isDefined && historian.currentTag.get.parentTag.isDefined =>
      val parQuery = historian.currentTag.get.parentTag.get.getQueryString
      regularSearch(parQuery)
      handleQuery(parQuery)
    case "." if historian.currentTag.isDefined =>
      val query = historian.currentTag.get.getQueryString
      handleQuery(query)
    case _ =>
  }


  def regularSearch(string: String) = {
    getMergedResult(string) match {
      case Some(result) =>
        resultHandler.apply(result)
        printTag(result)
      case _ =>
    }
  }

  def getResults(string: String) = historian.currentTag match {
    case Some(tag) =>
      val newQuery = tag.getQueryString + string
      searchEngine.getContentWithQuery(newQuery, Some(tag)) match {
        case list if list.nonEmpty => list
        case _ => searchEngine.getContentWithQuery(string)
      }
    case None => searchEngine.getContentWithQuery(string)
  }

  def getMergedResult(string: String) = getResults(string) match {
    case list if list.nonEmpty => Some(searchEngine.mergeResults(list))
    case _ => None
  }


  def getAutocompletionOptions(string: String): List[String] = historian.currentTag match {
    case Some(tag) => for {
      contentTag <- tag.getTagContents
      label = contentTag.getTitleString
      if label.startsWith(string)
    } yield label
    case _ => List.empty[String]

  }

  def printTag(tag: ContentTag, popResult: Boolean = true) = {
    terminal.clear
    if (popResult) historian.popSearchResult(tag)

    println(TagRenderer.getHierarchyDiagram(tag))
    println()
    println(renderer.renderTag(tag, terminal.width))
    println()
  }

}

