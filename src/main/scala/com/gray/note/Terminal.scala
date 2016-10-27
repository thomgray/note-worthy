package com.gray.note

import java.util

import scala.tools.jline._
import scala.tools.jline.console._
import scala.tools.jline.console.completer._


object Terminal {

  val terminal = TerminalFactory.create()
  val console = new ConsoleReader
  console.setPrompt("\u270E ")

  console.addCompleter(new Completer {
    override def complete(s: String, i: Int, list: util.List[CharSequence]): Int = {
      val options = MainController.getAutocompletionOptions(s)
      if (options.length==1) {
        console.putString(options.head.substring(s.length))
        0
      }
      else if (options.length>1) {
        console.print("\n"+options.mkString("\n")+"\n")
        console.drawLine()
        val remainder = finishStringWithAutocompleteOptions(options, s)
        console.putString(remainder)
        0
      }else {
        console.killLine()
        0
      }
    }
  })

  def finishStringWithAutocompleteOptions(autoCompletes: Array[String], string: String) = {
    val commonPrefix = getCommonStringPrefix(autoCompletes)
    if (commonPrefix.startsWith(string)) {
      commonPrefix.substring(string.length)
    } else ""
  }

  def getCommonStringPrefix(autocompletes: Array[String]): String = {
    var _string = autocompletes.headOption.getOrElse("")
    for (label <- autocompletes if !label.equals(_string)) {
      _string = getLargestCommonSubstring(label, _string)
    }
    _string
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

  def clear = console.clearScreen()
}
