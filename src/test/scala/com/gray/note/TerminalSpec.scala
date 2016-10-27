package com.gray.note

import org.scalatest._
import org.scalatest.MustMatchers._

class TerminalSpec extends FlatSpec {
  "Terminal" should "find the common substring to a string array" in {
    val strings = Array("poo", "poodle", "poop","poor")
    Terminal.getCommonStringPrefix(strings) mustBe "poo"
  }

  it should "find the common prefix to two strings" in {
    Terminal.getLargestCommonSubstring("idiot", "idiom") mustBe "idio"
  }
}