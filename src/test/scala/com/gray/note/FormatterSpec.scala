package com.gray.note

import com.gray.note.Config.{standardTab => TAB, tabEscapeString => ESC}
import org.scalatest.MustMatchers._

class FormatterSpec extends BaseSpec {

  "Formatter" should "indent properly" in {
    val string =
      """   This is an unindented string
        |Without indentation obvs
        |La la
        |""".stripMargin
    val indentedString = Formatter.indent(string, "    ")
    val expectedResult =
      """    This is an unindented string
        |    Without indentation obvs
        |    La la""".stripMargin
    indentedString mustBe expectedResult
  }

  it should "correctly include tabs for escape tab characters" in {
    var exampleString = s"${ESC}this is a test string"
    var expectedString = s"${TAB}this is a test string"
    Formatter.handleEscapeStrings(exampleString) mustBe expectedString

    exampleString = s"\\${ESC}this is a test string"
    expectedString = exampleString
    Formatter.handleEscapeStrings(exampleString) mustBe expectedString

    exampleString = s"""hello${ESC}is it me your looking for?"""
    expectedString = s"""hello${TAB}is it me your looking for?"""
    Formatter.handleEscapeStrings(exampleString) mustBe expectedString
  }

  it should "Keep the literal indent and remove the character if a line beging with a '|'" in {
    val string =
      """This is a string
        || this has one space
        ||  this has 2
        | |   3
        | |    4""".stripMargin
    val expected =
      """This is a string
        | this has one space
        |  this has 2
        |   3
        |    4""".stripMargin
    Formatter.indent(string, 0) mustBe expected
  }

  "Attributed String" should "intialize without fail" in {
    val as = new AttributedString()
    as.string mustBe ""
    val as1 = new AttributedString("Hello World!")
    as1.string mustBe "Hello World!"
  }

  it should "be mutable and add attributes on extension" in {
    val as = new AttributedString()
    as.string mustBe ""
    as += "Hello World!"
    as.string mustBe "Hello World!"
    as += (" This is a sting", Console.GREEN_B)
  }

  it should "store attributes for the string" in {
    val as = new AttributedString("Hello World!")
    as.addAttribute(Console.GREEN_B, parsing.Range(0, 5))
    as.attributes.length mustBe 1
    as.attributes(0) mustBe FormatAttribute(Console.GREEN_B, parsing.Range(0, 5))
  }


  it should "concatenate attributed strings" in {
    val as = new AttributedString()
    val as2 = new AttributedString()
    as += ("Hello", Console.GREEN)
    as += (" World!", Console.RED)
    as2 += (" Hi", Console.UNDERLINED)
    as2 += (" There!", Console.MAGENTA)

    val as3 = as + as2

    as3.attributes.length mustBe 4
    for (i <- as3.attributes.indices) {
      val at = as3.attributes(i)
      at.range mustBe (i match {
        case 0 => parsing.Range(0,5)
        case 1 => parsing.Range(5,12)
        case 2 => parsing.Range(12,15)
        case 3 => parsing.Range(15,22)
      })
    }
  }

  "FormatAttribute" should "shift" in {
    val fa = FormatAttribute(Console.MAGENTA, parsing.Range(1,5))
    val fa1 = fa.shift(5)

    fa1.range mustBe parsing.Range(6,10)
    fa1.format mustBe fa.format

    val fa2 = fa.shift(-1)

    fa2.range mustBe parsing.Range(0,4)
    fa2.format mustBe Console.MAGENTA
  }
}
