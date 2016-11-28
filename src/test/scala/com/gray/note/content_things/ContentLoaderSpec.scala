package com.gray.note.content_things

import org.scalatest.{FlatSpec, MustMatchers}

class ContentLoaderSpec extends FlatSpec with MustMatchers {

  val mdlLoader = MdlLoader

  "getContent with Mdl loader" should "return a single tag" in {
    val str =
      """[[[tag
        |content
        |]]]
      """.stripMargin
    val results = mdlLoader getContent(str)
    results.length mustBe 1
    results(0).getString mustBe "content"
  }

  it should "set the content of a content tag" in {
    val str =
      """[[[tag
        |
        |string
        |
        | [[[innertag
        | inner tag content
        | ]]]
        |
        |second string
        |
        |]]]
      """.stripMargin
    val tags = mdlLoader.getContent(str)
    tags.length mustBe 1
    val baseTag = tags.head.asInstanceOf[ContentTag]
    val contents = baseTag.getContents
    val gets = baseTag.get[ContentString]
    gets.map(_.paragraphs())
    contents.length mustBe 3
    contents(0) mustBe a [ContentString]
    contents(1) mustBe a [ContentTag]
    contents(2) mustBe a [ContentString]
  }

}
