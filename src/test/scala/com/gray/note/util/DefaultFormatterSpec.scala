//package com.gray.note.util
//
//import com.gray.markdown.formatting.{MdFormatter}
//import org.scalatest.{FlatSpec, MustMatchers}
//
//class DefaultFormatterSpec extends FlatSpec with MustMatchers {
//
//  val formatter = MdFormatter()
//
//  "wrapLines" should "wrap lines breaking at spaces" in {
//    val string = "this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
//    val expected = """this is a chunk of
//                     |text on a single
//                     |line which should
//                     |be wrapped by the
//                     |blank line blah
//                     |blah blah""".stripMargin
//    val strWrapped = formatter.wrapLines(string, 20)
//    strWrapped mustBe expected
//  }
//
//  it should "wrap lines breaking at hyphens" in {
//    val string = "this-is-a-chunk-of-text-on-a-single-line-which-should-be-wrapped-by-the-blank-line-blah-blah-blah"
//    val strWrapped = formatter.wrapLines(string, 30)
//    val expected = """this-is-a-chunk-of-text-on-a-
//                     |single-line-which-should-be-
//                     |wrapped-by-the-blank-line-
//                     |blah-blah-blah""".stripMargin
//    strWrapped mustBe expected
//  }
//
//  it should "wrap lines breaking at underlines" in {
//    val string = "this_is_a_chunk_of_text_on_a_single_line_which_should_be_wrapped_by_the_blank_line_blah_blah_blah"
//    val strWrapped = formatter.wrapLines(string, 30)
//    val expected = """this_is_a_chunk_of_text_on_a_
//                     |single_line_which_should_be_
//                     |wrapped_by_the_blank_line_
//                     |blah_blah_blah""".stripMargin
//    strWrapped mustBe expected
//  }
//
//  it should "wrap lines preserving indents" in {
//    val string = " this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
//    val strWrapped = formatter.wrapLines(string, 30)
//    val expected = """ this is a chunk of text on a
//                     | single line which should be
//                     | wrapped by the blank line
//                     | blah blah blah""".stripMargin
//    strWrapped mustBe expected
//  }
//
//  "formatString" should "wrap lines" in {
//    val string = "this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
//    val expected = """this is a chunk of
//                     |text on a single
//                     |line which should
//                     |be wrapped by the
//                     |blank line blah
//                     |blah blah""".stripMargin
//    val strWrapped = formatter.formatString(string, 20)
//    strWrapped mustBe expected
//  }
//
//
//  it should "default to braking words if a word break cannot be found" in {
//    val string = "thisisachinkoftextonasinglelinewhichshouldbewrappedbytheblanklineblahblahblah"
//    val strWrapped = formatter.wrapLines(string, 30)
//    val expected = """thisisachinkoftextonasinglelin
//                     |ewhichshouldbewrappedbytheblan
//                     |klineblahblahblah""".stripMargin
//    strWrapped mustBe expected
//  }
//
//  "padAndAlignBlock" should "right align and pad to the specified width" in {
//    val string =
//      """this is a
//        |string
//        |which I want to align
//        |rightly""".stripMargin
//    val raligned = formatter.padAndAlignBlock(string, align = "right")
//    raligned mustBe
//      """            this is a
//        |               string
//        |which I want to align
//        |              rightly""".stripMargin
//  }
//
//  "stitchString" should "stitch two string together when they are uniform width and equal length" in {
//    val str1 =
//      """this is a string|
//        |spanning three  |
//        |lines           |""".stripMargin
//    val str2 =
//      """this is another  |
//        |string that also |
//        |spans three lines|""".stripMargin
//    val str = formatter.stitchString(List(str1, str2))
//    str mustBe
//      """this is a string|this is another  |
//        |spanning three  |string that also |
//        |lines           |spans three lines|""".stripMargin
//
//  }
//
//  it should "stitch two strings together when they are of different lengths" in {
//    val str1 =
//      """this is
//        |a string
//        |spanning
//        |several
//        |lines""".stripMargin
//    val str2 = " 1. "
//    val str = formatter.stitchString(List(str2, str1))
//    str mustBe
//      """ 1. this is
//        |    a string
//        |    spanning
//        |    several
//        |    lines""".stripMargin
//  }
//}
