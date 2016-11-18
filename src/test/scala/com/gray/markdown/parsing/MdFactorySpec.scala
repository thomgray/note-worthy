package com.gray.markdown.parsing

import org.scalatest.{FlatSpec, MustMatchers}

class MdFactorySpec extends FlatSpec with MustMatchers {
  val factory = new DefaultMdFactory()
  import factory._

  "makeString" should "make a simple string on one line" in {
    val lines = List("hello world")
    val mdstr = makeMdString(lines)
    mdstr.toString mustBe "hello world"
  }

  it should "merge lines when not broken by a double space at the end" in {
    val lines = List("line 1", "line 2")
    val str = makeMdString(lines)
    str.toString mustBe "line 1 line 2"
  }
  it should "not merge lines when broken by a double space at the end" in {
    val lines = List("line 1  ", "line 2")
    val str = makeMdString(lines)
    str.toString mustBe "line 1\nline 2"
  }
  it should "remove irregular spacing" in {
    val lines = List("this   is    some text     and", "  this is   more text ")
    val str = makeMdString(lines)
    str.toString mustBe "this is some text and this is more text"
  }

}
