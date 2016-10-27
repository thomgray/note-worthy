package com.gray.util

import java.io.{File, FileNotFoundException, PrintWriter}


class IO(directoriesList: String) {
  val directoriesFile = directoriesList

  def getDirectories = io.Source.fromFile(directoriesFile).mkString.split("\n")

  def addDirectory(dir: String) = {
    var dirs = getDirectories
    if (!dirs.contains(dir)) {
      dirs = dirs :+ dir
      saveDirectories(dirs)
    }
  }

  def removeDirectory(dir: String) = {
    var dirs = getDirectories
    dirs = dir match {
      case d if d.startsWith("*") && d.endsWith("*") => dirs.filter(!_.contains(d.stripPrefix("*").stripSuffix("*")))
      case d if d.startsWith("*") => dirs.filter(! _.endsWith(d.stripPrefix("*")))
      case d if d.endsWith("*") => dirs.filter(! _.startsWith(d.stripSuffix("*")))
      case d => dirs.filter(!_.equals(d))
    }
    saveDirectories(dirs)
  }

  private def saveDirectories(dirs: Array[String]) = {
    val dirsString = dirs.mkString("\n")
    try {
      val writer = new PrintWriter(directoriesFile)
      writer.write(dirsString)
      writer.close()
    } catch {
      case e: FileNotFoundException => println("Directories file not found!")
    }
  }
}

object IO {
  def apply(directoriesList: String): IO = new IO(directoriesList)
}
