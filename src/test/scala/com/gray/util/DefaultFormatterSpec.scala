package com.gray.util

import com.gray.markdown.formatting.MdFormatter
import org.scalatest.{FlatSpec, MustMatchers}

class DefaultFormatterSpec extends FlatSpec with MustMatchers {

  "wrapLines" should "wrap lines breaking at spaces" in {
    val string = "this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
    val expected = """this is a chunk of
                     |text on a single
                     |line which should
                     |be wrapped by the
                     |blank line blah
                     |blah blah""".stripMargin
    val strWrapped = MdFormatter.wrapLines(string, 20)
    strWrapped mustBe expected
  }

  it should "wrap lines breaking at hyphens" in {
    val string = "this-is-a-chunk-of-text-on-a-single-line-which-should-be-wrapped-by-the-blank-line-blah-blah-blah"
    val strWrapped = MdFormatter.wrapLines(string, 30)
    val expected = """this-is-a-chunk-of-text-on-a-
                     |single-line-which-should-be-
                     |wrapped-by-the-blank-line-
                     |blah-blah-blah""".stripMargin
    strWrapped mustBe expected
  }

  it should "wrap lines breaking at underlines" in {
    val string = "this_is_a_chunk_of_text_on_a_single_line_which_should_be_wrapped_by_the_blank_line_blah_blah_blah"
    val strWrapped = MdFormatter.wrapLines(string, 30)
    val expected = """this_is_a_chunk_of_text_on_a_
                     |single_line_which_should_be_
                     |wrapped_by_the_blank_line_
                     |blah_blah_blah""".stripMargin
    strWrapped mustBe expected
  }

  it should "wrap lines preserving indents" in {
    val string = " this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
    val strWrapped = MdFormatter.wrapLines(string, 30)
    val expected = """ this is a chunk of text on a
                     | single line which should be
                     | wrapped by the blank line
                     | blah blah blah""".stripMargin
    strWrapped mustBe expected
  }

  "formatString" should "wrap lines" in {
    val string = "this is a chunk of text on a single line which should be wrapped by the blank line blah blah blah"
    val expected = """this is a chunk of
                     |text on a single
                     |line which should
                     |be wrapped by the
                     |blank line blah
                     |blah blah""".stripMargin
    val strWrapped = MdFormatter.formatString(string, 20)
    strWrapped mustBe expected
  }


  it should "default to braking words if a word break cannot be found" in {
    val string = "thisisachinkoftextonasinglelinewhichshouldbewrappedbytheblanklineblahblahblah"
    val strWrapped = MdFormatter.wrapLines(string, 30)
    val expected = """thisisachinkoftextonasinglelin
                     |ewhichshouldbewrappedbytheblan
                     |klineblahblahblah""".stripMargin
    strWrapped mustBe expected
  }
}
