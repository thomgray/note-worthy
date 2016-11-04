package com.gray.note.ui

import java.io.File

import scala.sys.process._

object Editor {
  def openVi = {
    val pr = Runtime.getRuntime.exec("vim /tmp/tmpfile")
    val exit = pr.waitFor
    val str = io.Source.fromFile(new File("/tmp/tmpfile")).mkString
    println(str)
  }
}
