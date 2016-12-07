package com.gray.note.ui

import java.util

import com.gray.note.Config

import scala.tools.jline.TerminalFactory
import scala.tools.jline.console.ConsoleReader
import scala.tools.jline.console.completer.Completer
import sys.process._


object Terminal {
  val terminal = TerminalFactory.create()
  val console = new ConsoleReader
  console.setKeyMap("vi")

  console.setPrompt("\u270E ")

  console.addCompleter(new Completer {
    override def complete(s: String, i: Int, list: util.List[CharSequence]): Int = {
      val options = MainController.getAutocompletionOptions(s.trim)
      if (s.trim startsWith Config.resetCurrentTagCommand){
        -1
      }else if (options.length==1) {
        console.putString(options.head.stripPrefix(s.trim))
        0
      }
      else if (options.length>1) {
        console.print("\n"+options.mkString("\t")+"\n")
        console.drawLine()
        val remainder = finishStringWithAutocompleteOptions(options, s)
        console.putString(remainder)
        0
      }else {
        -1
      }
    }
  })

  console.addCompleter(new Completer() {
    override def complete(s: String, i: Int, list: util.List[CharSequence]): Int = {
      val string = s.trim.stripPrefix("/").trim
      val baseTags = MainController.searchEngine.getBaseTags
      baseTags.map(_.getTitleString).filter(_.startsWith(string)) match {
        case list if list.length == 1 =>
          console.putString(list.head.stripPrefix(string.trim))
          0
        case list if list.length > 1 =>
          console.print("\n"+list.mkString("\t")+"\n")
          console.drawLine()
          val remainder = finishStringWithAutocompleteOptions(list, string)
          console.putString(remainder)
          0
        case _ => -1
      }
    }
  })

  console.addCompleter(new Completer {
    override def complete(s: String, i: Int, list: util.List[CharSequence]): Int =
      if (s.trim.startsWith(Config.urlOpenCommand)) {
        val string = s.trim.stripPrefix(Config.urlOpenCommand).trim
        val links = MainController.resultHandler.currentTagURLS.map(l=>l.inlineString.getOrElse(l.url))
        links.filter(_.startsWith(string)) match {
          case list if list.length == 1 =>
            console.putString(list.head.stripPrefix(string.trim))
            0
          case list if links.length > 1 =>
            console.print("\n"+list.mkString("\t")+"\n")
            console.drawLine()
            val remainder = finishStringWithAutocompleteOptions(list, string)
            console.putString(remainder)
            0
          case list => -1
        }

      0
    }else -1
  })

  def tryThis = {
    terminal.reset()
    console.flush()
    console.readLine()
    val process =  Runtime.getRuntime.exec(Array[String]("vi", "/tmp/tempFile"))
    terminal.reset()
    console.readLine()

  }


  def finishStringWithAutocompleteOptions(autoCompletes: List[String], string: String) = {
    val commonPrefix = getCommonStringPrefix(autoCompletes)
    if (commonPrefix.startsWith(string)) {
      commonPrefix.substring(string.length)
    } else ""
  }


  def getCommonStringPrefix(autocompletes: List[String]): String = autocompletes match {
    case head :: list  =>
      list.foldRight(head)(getLargestCommonSubstring)
    case _ => ""
  }


  def getLargestCommonSubstring(string1: String, string2: String): String = {
    val shortest = if (string1.length < string2.length) string1 else string2
    val longest = if (shortest==string1) string2 else string1

    for (i <- shortest.indices){
      val substring = shortest.substring(0, shortest.length-i)
      if (longest.startsWith(substring)) return substring
    }
    ""
  }

  def width = terminal.getWidth
  def height = terminal.getHeight

  def readLine = {
    console.readLine()
  }

  def clear = {
//    "printf \\033c".!
    "tput reset".!
  }

  def restore = terminal.restore()

  def runVi(): Unit ={
    val cmd =  "/Users/grayt13/Projects/note-worthy/src/main/script/runvi.sh"
    println("GOT HERE 1")
    val proc = Runtime.getRuntime.exec(Array("vim", "/tmp/test.txt"))
    println("GOT HERE 2")
    TerminalFactory.configure(TerminalFactory.UNIX)
    val exit = proc.waitFor()
    println(s"FINISHED WITH THE MAIN BIT. Exit code = ")
  }

  def special: Unit ={
    console.readCharacter()
  }

}
