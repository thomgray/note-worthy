package com.gray.note

object Config {
  val TEST = "test"
  val LIVE = "live"
  var environment = LIVE


  val liveDirectories = "/directories.txt"
  val testDirectories = "/test-directories.txt"

  val tabEscapeString = ">>"
  val standardTab = "    "

  val spitOutIndicator = ">"
  val dropBackCommand = ".."
  val remindCommand = "."

  val urlOpenCommand = "+"
  val resetCurrentTagCommand = "/"
}
