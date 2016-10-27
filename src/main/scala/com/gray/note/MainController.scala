package com.gray.note

import scala.collection.mutable
import scala.io.AnsiColor._
import scala.sys.process._
import java.nio.file.{Files, Paths}

import com.gray.note.content_things.ContentTag

object MainController extends Formatter {

  private val moveCommand = "mv"

  val handler = NoteHandler(Config.liveDirectories)
  val historian = SearchHistorian
  val terminal = Terminal

  def mainLoop = {
    var cont = true
    while (cont) {

      //val line = scala.io.StdIn.readLine().trim
      terminal.readLine.trim match {
        case "quit" | "exit" => cont = false
        case "-help" => printHelp
        case "" =>
        case line if line.startsWith(":") => handleCommand(line.substring(1).trim)
        case line => handleLine(line)
      }
    }
  }

  def handleCommand(line: String) = {
    line match {
      case "" =>
        val dirs = handler.io.getDirectories
        for (dir <- dirs) println(dir)
        handler.refreshNotes
      case s if s.startsWith("+") =>
        val newDir = s.stripPrefix("+").trim
        handler.io.addDirectory(newDir)
        handler.refreshNotes
      case s if s.startsWith("-") =>
        val str = s.stripPrefix("-").trim
        handler.io.removeDirectory(str)
        handler.refreshNotes
      case _ =>
    }
  }

  def handleLine(line: String) = {
    val (label, args) = handleArgs(line)

    (label, args, historian.currentTag) match {
      case (Config.dropBackCommand, _, Some(tag)) =>
        if (tag.parentTag.isDefined) doDropBack(tag, args) // drop back from the current tag
      case (Config.remindCommand, _, Some(tag)) =>
        printNote(tag)
      case ("", Some(arg), Some(tag)) => printNote(tag, Some(arg)) // do thing on the current tag
      case (_, _, current) => //normal case
        var commands = List[String]()
        var _label = label.trim

        if (label.startsWith(Config.spitOutIndicator)) {
          _label = _label.stripPrefix(Config.spitOutIndicator)
          commands = commands :+ moveCommand
        }

        val notes = current match  {
          case Some(currentTag) =>
            val furtherLabel = currentTag.getFullPath + " " + _label
            handler.tagsMatchingLabel(furtherLabel) match {
              case array if !array.isEmpty => array
              case array => handler.tagsMatchingLabel(_label)
            }
          case None => handler.tagsMatchingLabel(_label)
        }

        for (note <- notes) {
          printNote(note, args, commands)
        }
    }
  }

  private def handleArgs(string: String): (String, Option[String]) = {
    var outLine = string.trim
    val lastWord = outLine.split(" ").last

    if (lastWord.length > 0 && lastWord(0) == '-') {
      outLine = outLine.substring(0, outLine.length - lastWord.length).trim
      (outLine, Some(lastWord.substring(1)))
    } else {
      (outLine, None)
    }
  }

  private def doDropBack(tag: ContentTag, args: Option[String]) = {
    val parent = tag.parentTag.get
    historian.popSearchResult(parent)
    printNote(parent, args)
  }

  private def printNote(tag: ContentTag, arg: Option[String] = None, commands: List[String] = List()) = {
    arg match {
      case None =>
        if (checkAndOpenLink(tag)) {
          printSeparator
        } else {
          terminal.clear
          if (!commands.contains(moveCommand)) historian.popSearchResult(tag)
          println(getIndexAndHierarchyDiagram(tag) + "\n")
          println(tag)
        }
      case Some(argString) =>
        if (argString.contains("i") && argString.contains("c")) println(getIndexAndHierarchyDiagram(tag))
        else if (argString.contains("i")) println(getTopicHierarchyDiagram(tag))
        else if (argString.contains("c"))println(getIndexDiagram(tag))
        else if (argString.contains("l")) {
          println("Labels:")
          for (label <- tag.labels.get.sortWith((s1, s2) => {
            s1 < s2
          })) {
            println("   " + label.toLowerCase)
          }
        }
    }


  }
//
//  private def printNote(tag: ContentTag, args: Option[String] = None) = {
//    printSeparator
//    args match {
//      case None =>
//        if (checkAndOpenLink(tag)) {
//          //go back a step!
//        } else {
//          terminal.clear
//          println(getIndexAndHierarchyDiagram(tag) + "\n")
//          println(tag)
//        }
//      case Some(argString) =>
//        if (argString.contains("i") && argString.contains("c")) println(getIndexAndHierarchyDiagram(tag))
//        else if (argString.contains("i")) println(getTopicHierarchyDiagram(tag))
//        else if (argString.contains("c"))println(getIndexDiagram(tag))
//        else if (argString.contains("l")) {
//          println("Labels:")
//          for (label <- tag.labels.get.sortWith((s1, s2) => {
//            s1 < s2
//          })) {
//            println("   " + label.toLowerCase)
//          }
//        }
//    }
//  }

  def printSeparator = println("────────────────────────────────────────────────────────────────────────────────────")

  def printHelp = {
    println("You need help")
  }

  def getTopicHierarchyDiagram(tag: ContentTag) = {
    val linerHierarchy = getLinearTopicHierarchy(tag)
    var out = ""

    for (i <- linerHierarchy.indices) {
      val t = linerHierarchy(i)
      val prefix = i match {
        case 0 => "── "
        case _ => "└─ "
      }
      val title =
        if (i == linerHierarchy.length - 1) BOLD + t.getTitleString + RESET
        else t.getTitleString
      out = out + "\n" + indentSingleLine(prefix + title, i)
    }
    out.substring(1)
  }

  def getLinearTopicHierarchy(tag: ContentTag) = {
    val buffer = new mutable.MutableList[ContentTag]
    var _tag = tag
    buffer += tag
    var cont = _tag.parentTag.isDefined
    while (cont) {
      _tag = _tag.parentTag.get
      buffer += _tag
      cont = _tag.parentTag.isDefined
    }
    buffer.toList.reverse
  }

  def getIndexDiagram(tag: ContentTag) = {
    var _string = "── " + BOLD + tag.getTitleString + RESET
    val contents: Array[ContentTag] = tag.getTagContents

    for (i <- contents.indices) {
      val prefix =
        if (i == contents.length - 1) "└─ "
        else "├─ "
      _string = _string + "\n    " + prefix + contents(i).getTitleString
    }
    _string
  }

  //TODO fix exception caused by merging empty array when tag has no contents to list!
  def getIndexAndHierarchyDiagram(tag: ContentTag): String = {
    val hierarchyDiagream = getTopicHierarchyDiagram(tag)
    val contentsString = getIndexDiagram(tag)
    val contentStringLines = contentsString.split("\n").drop(1)

    if (contentStringLines.isEmpty) return hierarchyDiagream //quick fix

    val indent = getLinearTopicHierarchy(tag).length - 1
    val indentedIndexString = mergeStrings[String](contentStringLines, { s =>
      indentSingleLine(s, indent)
    })
    hierarchyDiagream + "\n" + indentedIndexString
  }

  private val PENCIL = "✏️"
  private val NOTEBOOK = "📖"


  def checkAndOpenLink(tag: ContentTag): Boolean = {
    val string = tag.getNeatBody
    var success = false
    if (isFile(string)) {
      //TODO get this to work! need to know the directory of the tag!
      for (dir <- handler.io.getDirectories if !success && Files.exists(Paths.get(dir + s"/_resources/$string"))){
        val absPath = dir + s"/_resources/$string"
        success = s"open $absPath".! == 0
      }
    } else if (isLink(string)) {
      success = s"open $string".! == 0
    }
    success
  }

  def isFile(string: String) = "^(\\S)*\\.\\w\\w\\w$".r.findFirstIn(string).isDefined

  def isLink(string: String) = "^(https://|http://)(\\S)*$".r.findFirstMatchIn(string).isDefined

  def getAutocompletionOptions(string: String) = {
    historian.currentTag match {
      case Some(tag) => for {
        contentTag <- tag.getTagContents
        label = contentTag.getTitleString
        if label.startsWith(string)
      } yield label

      case _ => Array[String]()
    }
  }
}
