package com.gray.note.content_things

import com.gray.parse.Location
import org.scalatest.{FlatSpec, Matchers}

class ContentTagAliasSpec extends FlatSpec with Matchers {

  "getAliasedQuery" should "be the string" in {
    val alias = ContentTagAlias("query", List("foo"))
    alias.getAliasedQuery shouldBe "query"
  }

  it should "inherit the query string of a parent tag" in {
    val alias = ContentTagAlias("query", List("foo"))
    val parent = ContentTag("", List("tag"), Location(0,0), List(alias))

    alias.getAliasedQuery shouldBe "tag query"
  }

}
