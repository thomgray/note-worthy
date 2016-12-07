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
  val commands = mutable.Map[String, String]()

  val resultHandler = ResultHandler
  val searchEngine = SearchEngine(Config.liveDirectories)

  implicit val renderer = new ContentRenderer() {}

  def specialCommand: Unit = {
    Terminal.special
  }

  def mainLoop = {
    var continue = true
    while (continue) {
      terminal.readLine.trim match {
        case str if str == "++" && historian.currentTag.isDefined => resultHandler.openTagInAtom(historian.currentTag.get)
        case str if str.trim.startsWith(resetCurrentTagCommand) =>
          historian.setCurrentTagToNone()
          val searchString = str.trim.stripPrefix(resetCurrentTagCommand).trim
          searchString match {
            case "" => terminal.clear
            case _ => regularSearch(searchString)
          }
        case str if str.matches("^:.*") => specialCommand
        case str if str.startsWith(Config.urlOpenCommand) && historian.currentTag.isDefined =>
          resultHandler.openURL(str.stripPrefix(urlOpenCommand).trim)
        case "exit" | "quit" => continue = false; terminal.restore
        case ".." if historian.currentTag.isDefined && historian.currentTag.get.parentTag.isDefined =>
          val newTag = historian.currentTag.get.parentTag.get
          printTag(newTag)
        case "." if historian.currentTag.isDefined =>
          val query = historian.currentTag.get.getQueryString
          regularSearch(query)
        case line if "^\\s*$".r.findFirstIn(line).isDefined =>
        case other => regularSearch(other)
      }
    }
  }

  def mainLoop2 = {
    var continue = true
    while (continue) {
      val rawLine = terminal.readLine
      val (query, args) = handleArgs(rawLine)

      continue = args.contains(EXIT_KEY)
      if (continue) doArgs(query, args)
    }
  }

  def handleArgs(string: String) = {
    var args = Map[String,Seq[String]]()
    var leftover = List.empty[String]

    WordIterator(string).iterate{ word =>
      if (word.startsWith("-")) {
        word match {
          case "-h" | "--help" => args = args + (HELP_KEY -> Seq.empty)
          case "-q" | "--quit" | "-e" | "--exit" => args = args + (EXIT_KEY -> Seq.empty)
          case _ =>
        }
      }else{
        leftover = leftover :+ word
      }
    }
    (leftover.mkString(" "), args)
  }

  def doArgs(query: String, map: Map[String, Seq[String]]) = {


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

