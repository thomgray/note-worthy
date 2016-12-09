package com.gray.note

object Config {
  val TEST = "test"
  val LIVE = "live"
  var environment = LIVE


  val liveRootDirectory = "/Users/grayt13/.note-worthy"
  val testRootDirectory = "/Users/grayt13/Projects/note-worthy/src/test/resources/fixtures/testRootDirectory"

  val tabEscapeString = ">>"
  val standardTab = "    "

  val spitOutIndicator = ">"
  val dropBackCommand = ".."
  val remindCommand = "."

  val urlOpenCommand = "+"
  val resetCurrentTagCommand = "/"
}
