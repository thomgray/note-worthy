package com.gray.markdown.parsing

import org.scalatest.{FlatSpec, MustMatchers}

class MdRegexesSpec extends FlatSpec with MustMatchers with MdRegexes {

  "MdLinkRegex" should "find referenced links" in {
    val str = " this is a string [google](wwww.google.com) and [github] (www.github.com)"
    val matches = MdLinkRegex.findAllMatchIn(str).toList
    matches.length mustBe 2
    matches(0).group(0) mustBe "[google](wwww.google.com)"
    matches(0).group(1) mustBe "[google]"
    matches(0).group(2) mustBe "(wwww.google.com)"
    matches(1).group(0) mustBe "[github] (www.github.com)"
    matches(1).group(1) mustBe "[github]"
    matches(1).group(2) mustBe " (www.github.com)"
  }

}
