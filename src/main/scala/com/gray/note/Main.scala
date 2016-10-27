package com.gray.note

import scala.io.AnsiColor._

object Main {
  Config.environment = Config.LIVE
//  val handler = NoteHandler(Config.liveNotesDirectory)
  val controller = MainController

  def main(array: Array[String]): Unit = {
    if (array.length == 0){
      printTitile
      controller.mainLoop
    }
    else{
      printHelp
    }
  }

  def printTitile = {
    print(BOLD)
    print(
      s"""
        |    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        | ║║║┃                NOTE WORTHY               ┃ ║║║
        |    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
      """.stripMargin)
    println(RESET)
  }
  def printHelp = {

  }
}