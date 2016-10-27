package com.gray.note

import MainController._
import org.scalatest.BeforeAndAfter

class MainControllerSpec extends BaseSpec with BeforeAndAfter {
  var handler = new TestNoteHandler
  val controller = MainController

  before {
    setNoteDirectory(fooNotesDirectory)
    handler = new TestNoteHandler
  }

  "MainController" should "find a linear tag hierarchy" in {
    val notes = handler.tagsMatchingLabel("fee fi fo fum")
    notes.length mustBe 1
    val hierarchy = controller.getLinearTopicHierarchy(notes(0))
    hierarchy.length mustBe 4
    hierarchy.head.getTitleString mustBe "fee"
    hierarchy.last.getTitleString mustBe "fum"
  }

  it should "find web urls and file paths" in {
    setNoteDirectory(linkNotesDirectory)
    handler = new TestNoteHandler
    val link = handler.tagsMatchingLabel("link").head
    val path = handler.tagsMatchingLabel("path").head

    MainController.isLink(link.getNeatBody) mustBe true
    MainController.isFile(path.getNeatBody) mustBe true
  }

  "isLink" should "identify valid urls" in {
    isLink("http://www.thisthing.com") mustBe true
    isLink("https://www.several_chickens.co.uk/contents?id=ijshdfkjhsdkjfh") mustBe true

    isLink("wwww.hobble .com") mustBe false
    isLink("ccc.blahblahblah.net") mustBe false
  }

  "isPath" should "identify valid file paths" in {
    isFile("/Users/kjndsf/ljsndf/jsndf-sdf.txt") mustBe true
    isFile("foo.txt") mustBe true
    isFile("sdljndkjfng/sdg") mustBe false
    isFile( "/sdf/ sdf / ") mustBe false
  }

  "isFile" should "recognise blah.txt as a file" in {
    isFile("blah.txt") mustBe true
  }

  it should "recognise \"this/blah/blah.pdf\" as a file" in {
    isFile("this/blah/blah.pdf") mustBe true
  }

  it should "not think \"blah txt\" is a file" in {
    isFile("blah txt") mustBe false
  }
  it should "think \"chinese-conversion-service-architechture.jpg\" is a file" in {
    isFile("chinese-conversion-service-architechture.jpg") mustBe true
  }

}