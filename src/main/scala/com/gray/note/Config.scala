package com.gray.note

object Config {
  val TEST = "test"
  val LIVE = "live"
  var environment = LIVE


  val liveDirectories = "src/main/resources/directories.txt"
  val testDirectories = "src/test/resources/test-directories.txt"

  val liveNotesDirectory = "/Users/grayt13/Documents/quickref_notes"
  val testNotesDirectory = new java.io.File(".").getCanonicalPath + "/src/test/scala/resources/stub_note_directories"

  val tabEscapeString = ">>"
  val standardTab = "    "

  val spitOutIndicator = ">"
  val dropBackCommand = ".."
  val remindCommand = "."
}
