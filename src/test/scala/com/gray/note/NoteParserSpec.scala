package com.gray.note

import com.gray.note.parsing.Range
import com.gray.note.parsing.StringIterator._

class NoteParserSpec extends BaseSpec {

  val testString = """     this is a string with begin index of 5. There is also a @(tag){here with some content  }"""
  val testString2 =
    """  This is a string with a @(tag;tag2){
      | here, with some content and a
      | @(nested tag) { with some nested content}
      |}
      |More string with @
      |(another tag)  {
      |     with more content
      |     }
      |}   """.stripMargin

  "String Iterator" should "find the first solid character in a string" in {
    val str = "      this string"
    assert(nextSolidCharIndex(0, str) == 6)
  }

  it should "find the index of a tad header" in {
    assert(getIndexOfNextTagHeader(0, testString) == Some(61))
  }

  it should "find the range of a braced expression" in {
    val str = "@(label) {inner bit}"
    assertResult(Some(parsing.Range(9, 20))) {
      getRangeOfBracedExpression(8, str)
    }
  }

  it should "find the range of a tag header" in {
    rangeOfNextTagHeader(0, testString) match {
      case Some(rng) =>
        val header = testString.substring(rng.start, rng.end)
        assertResult("@(tag)")(header)
      case _ => assert(false)
    }
  }

  it should "find the range of the next tag header after some text" in {
    assertResult(parsing.Range(61, 67)) {
      rangeOfNextTagHeader(0, testString).get
    }
    assertResult("@(tag)") {
      val rng = rangeOfNextTagHeader(0, testString)
      testString.substring(rng.get.start, rng.get.end)
    }
  }

  it should "find the range of a tag header and body" in {
    val rng = rangeOfNextTagHeader(0, testString)
    assert(rng.isDefined)
    val bodrng = getRangeOfBracedExpression(rng.get.end, testString)
    assert(bodrng.isDefined)

    assertResult("@(tag)") {
      testString.substring(rng.get.start, rng.get.end)
    }

    assertResult("{here with some content  }") {
      testString.substring(bodrng.get.start, bodrng.get.end)
    }
  }

  it should "find tag ranges for complex tags" in {
    assertResult(("@(tag;tag2)", "{\n here, with some content and a\n @(nested tag) { with some nested content}\n}")) {
      val rnges: Option[(parsing.Range, parsing.Range)] = rangeOfNextTagHeaderAndBody(0, testString2)
      val tag = rnges.get._1
      val bod = rnges.get._2
      (testString2.substring(tag.start, tag.end),
            testString2.substring(bod.start, bod.end))
    }
  }

  it should "find range of square braced bodies" in {
    val str = "this is a @(tag)[with square braced content ]"
    assertResult("[with square braced content ]") {
      getRangeOfBracedExpression(16, str) match {
        case Some(Range(x, y)) => str.substring(x, y)
        case _ => "fail"
      }
    }
  }

}
