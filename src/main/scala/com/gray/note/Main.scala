package com.gray.note

import com.gray.note.ui.{MainController, Terminal}

import scala.io.AnsiColor._

object Main {
  val mainController = MainController

  def main(array: Array[String]): Unit = {
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