package com.gray.markdown.formatting

import com.gray.util.attributed_string.AttributedString
import org.scalatest.{FlatSpec, MustMatchers}

class CodeColouringSpec extends FlatSpec with MustMatchers with CodeColouring {

  "segmentScala" should "segment triple quotes on a single line" in {
    val str = "this is a string with \"\"\"triple quotes\"\"\" innit?"
    val atString = AttributedString(str)
    val segmented = segmentScala(atString)
    segmented.length mustBe 3
    segmented(0).string mustBe "this is a string with "
    segmented(1).string mustBe "\"\"\"triple quotes\"\"\""
    segmented(2).string mustBe " innit?"
  }

  it should "segment triple quotes spanning multiple lines" in {
    val threeQuotes = "\"\"\""
    val str =
      s"""this is a string with $threeQuotes some
         |string spanning
         |multiple lines
         |$threeQuotes
         |and then some more string""".stripMargin
    val atString = AttributedString(str)
    val segments = segmentScala(atString)
    segments.length mustBe 3
    segments(0).string mustBe "this is a string with "
    segments(1).string mustBe
      s"""$threeQuotes some
         |string spanning
         |multiple lines
         |$threeQuotes""".stripMargin
    segments(2).string mustBe "\nand then some more string"
  }

}
