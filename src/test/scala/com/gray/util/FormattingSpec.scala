package com.gray.util

import org.scalatest._

class FormattingSpec extends FlatSpec with MustMatchers with Formatting {


  "trimEmptyLines" should "remove the empty lines before and after a string" in {
    val str =
      """
        |
        |line
        |
        |
        |
      """.stripMargin
    trimEmptyLines(str) mustBe "line"
  }

  it should "not remove whitespace at the beginning of a line" in {
    val str =
      """
        |
        |
        |   line
        |
      """.stripMargin
    trimEmptyLines(str) mustBe "   line"
  }

  "trueLength" should "measure the length of a string ignoring ansi attributes" in {
    val str = RED + "this" + RESET
    trueLength(str) mustBe 4
    trueLength(BLUE + BLUE_B + "hello") mustBe 5
    trueLength(ansiColours.mkString) mustBe 0
  }
}
