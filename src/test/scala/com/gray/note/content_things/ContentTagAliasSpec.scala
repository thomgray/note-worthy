package com.gray.note.content_things

import com.gray.markdown.{@@, MdHeader, MdString}
import com.gray.parse.Location
import org.scalatest.{FlatSpec, Matchers}

class ContentTagAliasSpec extends FlatSpec with Matchers {

  "getAliasedQuery" should "be the string" in {
    val alias = new ContentTagAlias("query", List("foo"), @@(0,0))
    alias.getAliasedQuery shouldBe "query"
  }

  it should "inherit the query string of a parent tag" in {
    val alias = new ContentTagAlias("query", List("foo"), @@(0,0))
    val parent = new ContentTag(List(alias), MdHeader(MdString("tag", @@(0,0)), 1, @@(0,0)), Nil, @@(0,0), "")

    alias.getAliasedQuery shouldBe "tag query"
  }

}
