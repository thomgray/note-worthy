package com.gray.note.ui

import java.io.File

import com.gray.note.Config
import java.nio.file.{Files, Paths}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Installer {

  implicit val context = scala.concurrent.ExecutionContext.Implicits.global

  val root = Config.liveRootDirectory
  val directoriesFile = root + "/directories.txt"
  val configFile = root + "/config.txt"



  def install(): Boolean = Await.result({
    for {
      _ <- assertRootDirectoryExists
      _ <- assertDirectoriesFileExists
      _ <- assertConfigFileExists
    } yield true
  }, 1 seconds)

  def assertRootDirectoryExists = if (!Files.exists(Paths.get(root))) {
    new File(root).mkdir() match {
      case true => Future.successful()
      case false => Future.failed(new Exception())
    }
  }else Future.successful()

  def assertDirectoriesFileExists = if (!Files.exists(Paths.get(directoriesFile))) {
    new File(directoriesFile).createNewFile() match {
      case true => Future.successful()
      case false => Future.failed(new Exception())
    }
  }else Future.successful()

  def assertConfigFileExists = if (!Files.exists(Paths.get(configFile))) {
    new File(configFile).createNewFile() match {
      case true => Future.successful()
      case false => Future.failed(new Exception())
    }
  }else Future.successful()

}
