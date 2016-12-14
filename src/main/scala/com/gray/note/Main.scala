package com.gray.note

import com.gray.note.ui.{Installer, MainController}

import scala.io.AnsiColor._

object Main {

  def main(array: Array[String]): Unit = {
    Installer.install()

    MainController.mainLoop
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