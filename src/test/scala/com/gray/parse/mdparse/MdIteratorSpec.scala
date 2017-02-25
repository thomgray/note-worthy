package com.gray.parse.mdparse

import com.gray.markdown.MdHeader
import com.gray.parse.{Location, ParseConstants, ParseResult}
import org.scalatest.{FlatSpec, Matchers}

class MdIteratorSpec extends FlatSpec with Matchers with ParseConstants {

  val iterator = MdIterator

  it should "load content tags from md formatted reading from the header" in {
    val str =
      """# header 1
        |some string""".stripMargin

    val results = iterator(str)
    results.length shouldBe 1
    results.head match {
      case ParseResult(string, labels, description, options, location) =>
        description shouldBe CONTENT_TAG
        string shouldBe "some string"
        labels.get.head should be("header 1")
        location shouldBe Location(0, 2)

      case _ => fail
    }
  }

  it should "load content strings from non-headed blocks" in {
    val str = "this is a string"
    val result = iterator(str)

    result match {
      case List(
        ParseResult("this is a string",
        None,
        CONTENT_STRING, _, _)) =>
      case _ => fail
    }
  }

  it should "load mixed content" in {
    val str =
      """this is a string
        |
        |so is this
        |
        |# this is a header
        |
        |this is the content of the header""".stripMargin

    val result = iterator(str)

    result.length shouldBe 2

    val res1 = result(0)
    val res2 = result(1)

    res1 match {
      case ParseResult(string, labels, description, _,_) =>
        string shouldBe
          """this is a string
            |
            |so is this
            |""".stripMargin
        description shouldBe CONTENT_STRING
    }

    res2 match {
      case ParseResult(string, labels, description, _,_) =>
        string shouldBe "\nthis is the content of the header"
        labels.get should contain("this is a header")
        description shouldBe CONTENT_TAG
      case _ => fail
    }


  }

  it should "read a series of headers" in {
    val str =
      """# header 1
        |some string
        |
        |# header 2
        |some other string
        |
        |# header 3
        |yet another""".stripMargin

    val result = iterator(str)
    result.length shouldBe 3

    result(0) match {
      case ParseResult(string, Some(labels), CONTENT_TAG, options, Location(start, end, _,_)) =>
        string shouldBe "some string\n"
        labels.head shouldBe "header 1"
        start shouldBe 0
      case _ => fail
    }
    result(1) match {
      case ParseResult(string, Some(labels), CONTENT_TAG, options, Location(start, end, _,_)) =>
        string shouldBe "some other string\n"
        labels.head shouldBe "header 2"
        start shouldBe 3
      case _ => fail
    }
    result(2) match {
      case ParseResult(string, Some(labels), CONTENT_TAG, options, Location(start, end, _,_)) =>
        string shouldBe "yet another"
        labels.head shouldBe "header 3"
        start shouldBe 6
      case _ => fail
    }
  }

  it should "read headers with inner headers" in {
    val str =
      """
        |# hello there
        |this is content
        |
        |## inner header
        |
        |more content""".stripMargin

    val result = iterator(str)

    result.length shouldBe 1
    result.head match {
      case ParseResult(string, Some(labels), CONTENT_TAG, _, Location(start, end, _, _)) =>
        string shouldBe
          """this is content
            |
            |## inner header
            |
            |more content""".stripMargin
        start shouldBe 1
        end shouldBe 7
      case _ => fail
    }
  }


  "readMdHeader" should "find a header" in {
    iterator.readMdHeader("## this is a header") shouldBe Some(MdHeader("this is a header", 2))
  }

  it should "not find a header where ther isn't one" in {
    iterator.readMdHeader("this is a line") shouldBe None
  }

  "indexOfNextHeader" should "find the index any header when tier = 0" in {
    val lines =
      """one
        |two
        |# three
        |four
      """.stripMargin.split("\n")

    iterator.indexOfNextHeader(0, lines, 0) shouldBe Some((2, MdHeader("three", 1)))
  }

  it should "find the index of the next header at the specified tier" in {
    val str =
      """one
        |two
        |## three
        |four
      """.stripMargin.split("\n")

    iterator.indexOfNextHeader(0, str, 2) shouldBe Some((2, MdHeader("three", 2)))
  }

  it should "find the index of the next header with tier less than the specified tier" in {
    val str =
      """one
        |two
        |# three
        |four
      """.stripMargin.split("\n")

    iterator.indexOfNextHeader(0, str, 5) shouldBe Some((2, MdHeader("three", 1)))
  }

  "rangeOfNextHeaderBlock" should "find the range of a header block bounded by another header" in {
    val lines =
      """nothing
        |# hello
        |hello there
        |this is content
        |# header2
        |blah""".stripMargin.split("\n")
    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1, 4, MdHeader("hello", 1)))
  }

  it should "find the range of a header block when not bounded by another header" in {
    val lines =
      """nothing
        |# hello
        |hello there
        |this is content""".stripMargin.split("\n")
    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1, 4, MdHeader("hello", 1)))
  }

  it should "find the range of a header block when the block contatins sub headers" in {
    val lines =
      """nothing
        |# hello
        |## subheader
        |content
        |## subheader2
        |hello there
        |this is content""".stripMargin.split("\n")

    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1, 7, MdHeader("hello", 1)))
  }

  it should "find adjacent headers" in {
    val lines =
      """# header 1
        |sdfsdf
        |dfsdfsf
        |
        |# header 2
        |dsfsfd
        |sdfsf""".stripMargin.split("\n")
    val range1 = iterator.rageOfNextHeaderBlock(0, lines)
    val range2 = iterator.rageOfNextHeaderBlock(range1.get._2, lines)

    range1 shouldBe Some((0, 4, MdHeader("header 1", 1)))
    range2 shouldBe Some(4, 7, MdHeader("header 2", 1))
  }

  "nextThing" should "find the next block corrected by the offset in" in {
    val lines =
      """# header1
        |this is a thing
        |this is another
        |# header2
        |this is stiff
        |## header sub
        |sdfsdf
        |# header3
        |sdfsdf """.stripMargin.split("\n")


    val p1 = iterator.nextThingFrom(0, lines, 0)
    val p2 = iterator.nextThingFrom(p1.get._2, lines, 0)
    val p3 = iterator.nextThingFrom(p2.get._2, lines, 0)
    val p4 = iterator.nextThingFrom(p3.get._2, lines, 0)

    p1.get._1.head.labels.get.head shouldBe "header1"
    p1.get._1.head.location shouldBe Location(0, 3, 0, 0)
    p1.get._1.head.description shouldBe CONTENT_TAG
    p2.get._1.head.labels.get.head shouldBe "header2"
    p2.get._1.head.location shouldBe Location(3, 7, 0, 0)
    p2.get._1.head.description shouldBe CONTENT_TAG
    p3.get._1.head.labels.get.head shouldBe "header3"
    p3.get._1.head.location shouldBe Location(7, 9, 0, 0)
    p3.get._1.head.description shouldBe CONTENT_TAG
    p4 shouldBe None
  }

  it should "find optional label" in {
    val str =
      """# header [with options;other option]
        |blah
        |blah""".stripMargin

    val result = iterator(str).head
    val labels = result.labels

    labels shouldBe defined
    labels.get(0) shouldBe "header"
    labels.get(1) shouldBe "with options"
    labels.get(2) shouldBe "other option"
  }

  it should "find tag aliases" in {
    val str = """[alias]<aliased content>"""
    val results = iterator(str)
    results.length shouldBe 1
    results.head.description shouldBe CONTENT_ALIAS
    results.head.string shouldBe "aliased content"
    results.head.labels shouldBe Some(List("alias"))
  }
}
