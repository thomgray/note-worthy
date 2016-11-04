package com.gray.util

import org.scalatest._
import org.scalatest.MustMatchers._

class FormattingSpec extends FlatSpec with MustMatchers {

  val formatter = new Formatting{}

  "trimEmptyLines" should "remove the empty lines before and after a string" in {
    val str =
      """
        |
        |line
        |
        |
        |
      """.stripMargin
    formatter.trimEmptyLines(str) mustBe "line"
  }

  it should "not remove whitespace at the beginning of a line" in {
    val str =
      """
        |
        |
        |   line
        |
      """.stripMargin
    formatter.trimEmptyLines(str) mustBe "   line"
  }

}
