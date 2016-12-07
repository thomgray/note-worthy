package com.gray.mixin

import org.scalatest.{FlatSpec, MustMatchers}

class ListUtilSpec extends FlatSpec with ListUtil with MustMatchers{

  "trimBlankLinesFromList()" should "trim blank lines from a list of strings" in {
    val list = List("", "", "hello", "there", "")
    trimBlankLinesFromList(list) mustBe List("hello", "there")
  }

  it should "note remove blank lines in between lines" in {
    val list = List("this", "", "  ", "is")
    trimBlankLinesFromList(list) mustBe List("this", "", "  ", "is")
  }


}
