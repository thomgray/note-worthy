package com.gray.note

import org.scalatest.BeforeAndAfter
import org.scalatest.MustMatchers._

class NoteHandlerSpec extends BaseSpec with BeforeAndAfter{
  setNoteDirectory(fooNotesDirectory)
  var handler: TestNoteHandler = new TestNoteHandler

  before {
    setNoteDirectory(fooNotesDirectory)
    handler.refreshNotes// = new TestNoteHandler
  }

  "Config Environment" should "be set to TEST" in {
    Config.environment mustBe "test"
  }

  "Note Handler" should "initialise all note files with root tags" in {
    setNoteDirectory(initNotesDirectory)
    handler.refreshNotes
    
    handler.baseTags.length mustBe 3
    for (i <- handler.baseTags.indices) {
      handler.baseTags(i).labels.get.head mustBe (i match{
        case 0 => "note1"
        case 1 => "note5"
        case 2 => "note6"
        case _ => assert(false)
      })
    }
  }

  it should "iterate all tags" in {
    setNoteDirectory(initNotesDirectory)
    handler.refreshNotes
    
    val allTags = handler.getAllTags
    allTags.length mustBe 6
    for (i <- allTags.indices) {
      val tag = allTags(i)

      tag.labels.get(0) mustBe (i match {
        case 0 => "note1"
        case 1 => "note2"
        case 2 => "note4"
        case 3 => "note3"
        case 4 => "note5"
        case 5 => "note6"
      })
    }
  }

  it should "get all aliased tags" in {
    val aliases = handler.getAllParaphraseTags
    aliases.length >=4 mustBe true
  }

  it should "get all aliased tags matching a label" in {
    val aliases = handler.getAllParaphraseTags("drop")
    aliases.length mustBe 1
    aliases(0).labels.get(0) mustBe "drop"

    setNoteDirectory(aliasNotesDirectory)
    handler.refreshNotes
    
    val aliases4a = handler.getAllParaphraseTags("4a")
    aliases4a.length mustBe 1
    aliases4a(0).getFullPath mustBe "1 2 3 4"
  }

  it should "find a non-nested tag matching label" in {
    val matches = handler.tagsMatchingLabel("bar")
    matches.length mustBe 1
    matches(0).labels.get(0) mustBe "bar"
  }

  it should "find a nested tag matching label" in {
    val matches = handler.tagsMatchingLabel("foo green things")
    matches.length mustBe 1
    matches(0).labels.get(0) mustBe "green things"
  }

  it should "find a nested, universally referenced tag matching label" in {
    val matches = handler.tagsMatchingLabel("urt")
    matches.length mustBe 1
    matches(0).labels.get(0) mustBe "ur tag"
  }

  it should "find a tag if the search is in upper case but the label is in lower case" in {
    setNoteDirectory(caseNotesDirectory)
    handler.refreshNotes
    val matches = handler.tagsMatchingLabel("Lower Case")
    matches.length mustBe 1
  }

  it should "find a tag if the label is in upper case but the search is in lower case" in {
    setNoteDirectory(caseNotesDirectory)
    handler.refreshNotes
    val matches = handler.tagsMatchingLabel("upper case")
    matches.length mustBe 1
  }



  "Aliasing" should "reference a note with a base alias and a full path" in {
    setNoteDirectory(aliasNotesDirectory)
    handler.refreshNotes
    val matches = handler.tagsMatchingLabel("1a")
    matches.length mustBe 1
    matches(0).labels.get(0) mustBe "1"
  }

  it should "reference a note with a nested alias and a full path" in {
    setNoteDirectory(aliasNotesDirectory)
    handler.refreshNotes
    val matches = handler.tagsMatchingLabel("1 2 4a")
    matches.length mustBe 1
    matches(0).labels.get(0) mustBe "4"
    matches(0).getFullPath mustBe "1 2 3 4"
  }

  it should "yield its alias path when queried for its 'fullPath' property" in {
    setNoteDirectory(aliasNotesDirectory)
    handler.refreshNotes
    val tags = handler.getAllParaphraseTags
    for (tag <- tags) {
      tag.getFullPath mustBe (tag.labels.get(0) match {
        case "4a" => "1 2 3 4"
        case "3a" => "1 2 3"
        case "1a" => "1"
        case "6a" => "1 5 6"
      })
    }
  }


}

class TestNoteHandler extends NoteHandler(Config.testDirectories)