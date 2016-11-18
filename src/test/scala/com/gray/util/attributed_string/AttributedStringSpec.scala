package com.gray.util.attributed_string

import com.gray.util.Ranj
import org.scalatest.{FlatSpec, MustMatchers}

import scala.io.AnsiColor._

class AttributedStringSpec extends FlatSpec with MustMatchers{

  "+/" should "append the argument as a new line" in {
    val string1 = AttributedString("This is the first line", Seq(RED, BOLD))
    val string2 = AttributedString("This is the second line", Seq(CYAN, YELLOW_B))

    val tied = string1 +/ string2
    val lines = tied.string.split("\n")
    lines mustBe List("This is the first line", "This is the second line")
    tied.splitLines.length mustBe 2
    tied.splitLines(0).attributes.toList mustBe List(Attribute(Ranj(0,22),Seq(RED, BOLD)))
    tied.splitLines(1).attributes.toList mustBe List(Attribute(Ranj(0,23),Seq(CYAN, YELLOW_B)))
  }

  "findFirst" should "find the matching regex" in {
    val atstr = AttributedString("This is a bit of attributed string", Seq(RED, BOLD))
    val regex = s"""^.{0,30}(\\s|_|-)""".r
    val regMatch = atstr.findFirst(regex)
    regMatch.get.string mustBe "This is a bit of attributed "
  }

  "trimLeading" should "string whitespace from the front of a line" in {
    val str = "    hello"
    AttributedString(str).trimLeading.string mustBe "hello"
  }

  "trimTrailing" should "strip whitespace from the end of a string" in {
    val str = "  hello     "
    AttributedString(str).trimTrailing.string mustBe "  hello"
  }

  "wrapLines" should "wrap lines by breaking spaces" in {
    val attributedString = AttributedString("this is an attributed string that is greater than 20 characters")
    val wrapped = attributedString.wrapLines(30)
    wrapped.string mustBe
      """this is an attributed string
        |that is greater than 20
        |characters""".stripMargin
  }

  it should "" in {
    val str = "println(this + another + s\"$this $another\" + \"\\\" more strings\") and then some"
    val atstr= AttributedString(str, Seq(BLACK_B, WHITE))
    val wrapped = atstr.wrapLines(150, Some(AttributedString("->")))
  }

  "padTo" should "pad the string to the specified width" in {
    val str = AttributedString("hello")
    val str1 = str.padTo(30)
    str1.length mustBe 30
  }

  it should "pad with the specified attributes" in {
    val str = AttributedString("hi")
    val padstr = AttributedString(" ", Seq(RED_B))
    val out = str.padTo(30, " ", List(RED_B))
    //TODO assert something!
  }

  "addAttribute" should "add an attribute to the whole string a range if not specified" in {
    val str = AttributedString("hello there")
    val str1 = str.addAttribute(Seq(RED))
    str1.attributes.toList.length mustBe 1
    str1.attributes.toList.head.range mustBe Ranj(0, 11)
  }

  it should "work when you specify an attribute in a range where one already exists" in {
    val str = AttributedString("hello there", Seq(BLUE))
    val str1 = str.addAttribute(Seq(RED))
    str1.attributes.toList.length mustBe 1
    str1.attributes.toList.head.range mustBe Ranj(0, 11)
    str1.attributes.toList.head.formats.foregroundColor mustBe Some(RED)
  }
}
