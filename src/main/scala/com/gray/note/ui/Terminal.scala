package com.gray.note.ui

import java.awt.event.{ActionEvent, ActionListener}
import java.util

import apple.laf.JRSUIUtils.InternalFrame

import scala.tools.jline.TerminalFactory
import scala.tools.jline.console.{ConsoleReader, KeyMap}
import scala.tools.jline.console.completer.Completer
import scala.tools.jline.console.internal.ConsoleRunner


object Terminal {
  val terminal = TerminalFactory.create()
  val console = new ConsoleReader
  console.setKeyMap("vi")

  console.setPrompt("\u270E ")

  console.addCompleter(new Completer {
    override def complete(s: String, i: Int, list: util.List[CharSequence]): Int = {
      val options = MainController.getAutocompletionOptions(s.trim)
      if (options.length==1) {
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
        console.killLine()
        0
      }
    }
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

  def getCommonStringPrefix(autocompletes: List[String]): String = {
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
//    val input = console.getInput
//    input.read() match {
//      case 'c' => print("C")
//      case _ =>
//    }
    console.readLine()
  }

  def clear = {
    console.clearScreen()
  }

  def restore = terminal.restore()

  def reset = TerminalFactory.reset()

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
