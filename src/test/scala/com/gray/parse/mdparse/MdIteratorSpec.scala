package com.gray.parse.mdparse

import com.gray.markdown.MdHeader
import com.gray.parse.Location
import org.scalatest.{FlatSpec, Matchers}

class MdIteratorSpec extends FlatSpec with Matchers {

  val iterator = MdIterator

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

    iterator.indexOfNextHeader(0, str,2) shouldBe Some((2, MdHeader("three", 2)))
  }

  it should "find the index of the next header with tier less than the specified tier" in {
    val str =
      """one
        |two
        |# three
        |four
      """.stripMargin.split("\n")

    iterator.indexOfNextHeader(0, str,5) shouldBe Some((2, MdHeader("three", 1)))
  }

  "rangeOfNextHeaderBlock" should "find the range of a header block bounded by another header" in {
    val lines =
      """nothing
        |# hello
        |hello there
        |this is content
        |# header2
        |blah""".stripMargin.split("\n")
    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1,4, MdHeader("hello", 1)))
  }

  it should "find the range of a header block when not bounded by another header" in {
    val lines =
      """nothing
        |# hello
        |hello there
        |this is content""".stripMargin.split("\n")
    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1,4, MdHeader("hello", 1)))
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

    iterator.rageOfNextHeaderBlock(0, lines) shouldBe Some((1,7, MdHeader("hello", 1)))
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

    p1.get._1.labels.get.head shouldBe "header1"
    p1.get._1.location shouldBe Location(3, 6, 0, 0)
    p2.get._1.labels.get.head shouldBe "header2"
    p2.get._1.location shouldBe Location(6, 10, 0, 0)
    p3.get._1.labels.get.head shouldBe "header3"
    p3.get._1.location shouldBe Location(10, 12, 0, 0)
    p4 shouldBe None
  }


}
