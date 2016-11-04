package com.gray.note

object Config {
  val TEST = "test"
  val LIVE = "live"
  var environment = LIVE


  val liveDirectories = "src/main/resources/directories.txt"
  val testDirectories = "src/test/resources/test-directories.txt"

  val tabEscapeString = ">>"
  val standardTab = "    "

  val spitOutIndicator = ">"
  val dropBackCommand = ".."
  val remindCommand = "."
}
