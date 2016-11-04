package com.gray.markdown.parsing

import com.gray.markdown.MdBulletedListItem
import org.scalatest.{FlatSpec, MustMatchers}


class MdFactorySpec extends FlatSpec with MustMatchers with MdFactory {

  "makeList" should "make a simple list on one tier" in {
    val lines = List("- one", "- two", "- three")
    val list = makeList(lines)
    list.items.length mustBe 3
    for (item <- list.items) item.tier mustBe 0
  }

  it should "make a simple list on several tiers" in {
    val lines = List("- one", "  - two", "    - three", "      - four")
    val list = makeList(lines)
    list.items.length mustBe 4
    for (item <- list.items) item.tier mustBe list.items.indexOf(item)
  }

  it should "make a list with multiline items" in {
    val lines = List("- one","one.one", "- two","two.one")
    val list = makeList(lines)
    val item1 = list.items(0).asInstanceOf[MdBulletedListItem]
    val item2 = list.items(1).asInstanceOf[MdBulletedListItem]
    list.items.length mustBe 2

    item1.string.toString mustBe "one one.one"
    item2.string.toString mustBe "two two.one"

    item1.tier mustBe 0
    item2.tier mustBe 0
  }


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
