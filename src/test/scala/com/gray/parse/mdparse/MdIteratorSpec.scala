package com.gray.parse.mdparse

import com.gray.markdown.{@@, MdHeader, MdString}
import com.gray.note.content_things.ContentString
import com.gray.parse._
import org.scalatest.{FlatSpec, Matchers}

class MdIteratorSpec extends FlatSpec with Matchers with ParseConstants {
  import scala.language.implicitConversions

  val iterator = MdIterator
  implicit def stringToMdString(string: String): MdString = MdString(string, @@(0,string.split("\n").length))

  it should "load content tags from md formatted reading from the header" in {
    val str =
      """# header 1
        |some string""".stripMargin

    val results = iterator(str)
    results.length shouldBe 1
    results.head match {
      case TagParseResult(content, header, altLabels) =>
        content.length shouldBe 1
        header.mdString.string shouldBe "header 1"
        altLabels shouldBe Nil
      case _ => fail
    }
  }

  it should "load content strings from non-headed blocks" in {
    val str = "this is a string"
    val result = iterator(str)

    result match {
      case List(StringParseResult(pars)) =>
        pars.length shouldBe 1
        pars.head shouldBe a [MdString]
        pars.head.asInstanceOf[MdString].string shouldBe "this is a string"
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
      case StringParseResult(pars) =>
        pars.length shouldBe 2
        pars.head.asInstanceOf[MdString].string shouldBe "this is a string"
        pars.last.asInstanceOf[MdString].string shouldBe "so is this"
      case _ => fail
    }

    res2 match {
      case TagParseResult(content, header, labels) =>
        content.length shouldBe 1
        labels shouldBe Nil
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

    result(0) shouldBe a [TagParseResult]
    result(1) shouldBe a [TagParseResult]
    result(2) shouldBe a [TagParseResult]
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
      case TagParseResult(content, header, _) =>
        content.length shouldBe 2
      case _ => fail
    }
  }

  "getAltLabelsFromHeader" should "return the header with nil if no alt labels are stated" in {
    val header = MdHeader(MdString("header", @@(0,0)), 1, @@(0,0))
    iterator.getAlLablesFromHeader(header) shouldBe (header, Nil)
  }

  it should "split an alt-labeled header into a new header with alt labels" in {
    val header = MdHeader(MdString("header [label;\"label2\"]", @@(0,0)), 1, @@(0,0))
    val split = iterator.getAlLablesFromHeader(header)
    split._1.mdString.string shouldBe "header"
    split._2 shouldBe List("label", "\"label2\"")
  }

  "headerAltLabelRegex" should "match header with altLabels" in {
    iterator.headerAltLabelsRegex.findFirstMatchIn("header [altlabel]") shouldBe defined
  }

}
