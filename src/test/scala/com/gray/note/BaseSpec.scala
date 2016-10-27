package com.gray.note

import java.io.{FileNotFoundException, PrintWriter}

import org.scalatest._
import Config._

abstract class BaseSpec extends FlatSpec with MustMatchers {
  environment = TEST

  val initNotesDirectory: String = Config.testNotesDirectory+"/initNotes"
  val caseNotesDirectory = Config.testNotesDirectory+"/caseNotes"
  val aliasNotesDirectory = Config.testNotesDirectory+"/aliasNotes"
  val fooNotesDirectory = Config.testNotesDirectory+"/fooNotes"
  val linkNotesDirectory = Config.testNotesDirectory+"/link_path_notes"

  def setNoteDirectory(dirs: String*) = {
    val dirString = dirs.mkString("\n")
    try {
      val writer = new PrintWriter(Config.testDirectories)
      writer.write(dirString)
      writer.close()
    } catch {
      case e: FileNotFoundException => println("Directories file not found!")
    }
  }

}