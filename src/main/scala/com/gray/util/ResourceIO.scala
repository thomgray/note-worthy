package com.gray.util

import java.io.{File, FileNotFoundException, PrintWriter}

trait IO {
  val noteWorthyRootDirectory: String

  lazy val directoriesListFilePath = noteWorthyRootDirectory +
      (if (noteWorthyRootDirectory.endsWith("/")) "" else "/") +
      "directories.txt"


  def getDirectories = scala.io.Source.fromFile(directoriesListFilePath).mkString.split("\n").toList


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

  private def saveDirectories(dirs: List[String]) = {
    val dirsString = dirs.mkString("\n")
    try {
      val writer = new PrintWriter(new File(directoriesListFilePath))
      writer.write(dirsString)
      writer.close()
    } catch {
      case e: FileNotFoundException => println("Directories file not found!")
    }
  }
}

class ResourceIO(directoriesListPath: String) extends IO {
  override val noteWorthyRootDirectory: String = directoriesListPath

  new File(noteWorthyRootDirectory).exists() match {
    case true => println("the root file exists!")
    case false => println(s"no root file apparently: $noteWorthyRootDirectory")
  }
}
