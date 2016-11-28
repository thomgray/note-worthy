package com.gray.note

import com.gray.note.ui.MainController

import scala.io.AnsiColor._

object Main {
  val mainController = MainController

  def main(array: Array[String]): Unit = {
//    mainController.terminal.runVi()
    mainController.mainLoop
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