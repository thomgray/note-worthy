package com.gray.markdown.formatting

import org.scalatest.MustMatchers._
import org.scalatest._

import scala.io.AnsiColor

class CodeParserSpec extends FlatSpec with AnsiColor {
  val testParser = new CodeColouring() {}
  "splitCodeAndStringScala" should "separate strings from code" in {
    val str =  """this is code and "this is string" and this is more code s"and this is more string""""
    val split = testParser.splitCodeAndStringsScala(str)
    split.length mustBe 4
    split(0) mustBe "this is code and "
    split(1) mustBe "\"this is string\""
    split(2) mustBe " and this is more code "
    split(3) mustBe "s\"and this is more string\""
  }

  it should "separate comments from strings and code" in {
    val str =
      """this is code, ///this is a comment
        |"and this is string"""".stripMargin
    val split = testParser.splitCodeAndStringsScala(str)
    split.length mustBe 3
    split(0) mustBe "this is code, "
    split(1) mustBe "///this is a comment\n"
    split(2) mustBe "\"and this is string\""
  }


}