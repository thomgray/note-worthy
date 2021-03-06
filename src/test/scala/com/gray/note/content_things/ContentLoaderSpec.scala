package com.gray.note.content_things

import com.gray.markdown.{@@, MdHeader, MdLocation, MdString}
import org.scalatest.{FlatSpec, MustMatchers}

class ContentLoaderSpec extends FlatSpec with MustMatchers {

  val mdlLoader = MdlLoader
  val nowhere = @@(0,0)

  "getContent with Mdl loader" should "return a single tag" in {
    val str =
      """[[[tag
        |content
        |]]]
      """.stripMargin
    val results = mdlLoader getContent str
    results.length mustBe 1
    results.head mustBe a [ContentTag]
    val tag = results.head.asInstanceOf[ContentTag]
    tag.contents.length mustBe 1
    tag.contents.head mustBe a [ContentString]
    val contentString = tag.contents.head.asInstanceOf[ContentString]
    contentString.paragraphs.length mustBe 1
    contentString.paragraphs.head mustBe a [MdPlainString]
    contentString.paragraphs.head.asInstanceOf[MdPlainString].string mustBe "content"
    tag.header.mdString.string mustBe "tag"
    tag.altLabels mustBe Nil
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
    val contents = baseTag.contents
    val gets = baseTag.get[ContentString]

    contents.length mustBe 3
    contents(0) mustBe a[ContentString]
    contents(1) mustBe a[ContentTag]
    contents(2) mustBe a[ContentString]
  }

  ignore should "recurse the offset for nested tags" in { // move this to the parser, as the parser does this now
    val str =
      """[[[tag
        |content
        |[[[innertag
        |inner content
        |]]]
        |]]]""".stripMargin
    val result = mdlLoader.getContent(str)
    val outer = result.head.asInstanceOf[ContentTag]
    val inner = outer.getTagContents.head

    outer.location.startLine mustBe 0
    inner.location.startLine mustBe 2
  }

  val markdownLoader = MdLoader

  "MdLoader" should "load a string with headers as tags" in {
    val str =
      """# main bit
        |
        |some content
        |
        |
        |# next bit
        |
        |more content
        |# third bit
        |third bit of content
      """.stripMargin

    val results = markdownLoader.getContent(str)

    results.length mustBe 3
    results(0) mustBe a[ContentTag]
    results(1) mustBe a[ContentTag]
    results(2) mustBe a[ContentTag]
  }

  ignore should "maintain the offset for nested content" in { // move to parser spec

    val str =
      """# main bit
        |
        |some content
        |
        |
        |## next bit
        |
        |more content
        |### third bit
        |third bit of content""".stripMargin

    val results = markdownLoader.getContent(str).head.asInstanceOf[ContentTag]
    results.location.startLine mustBe 0
    results.location.endLine mustBe 10

    results.getTagContents.head.location mustBe MdLocation(5, 10)

    results.getTagContents.head.getTagContents.head.location mustBe MdLocation(8, 10)
  }

  it should "get content tags and string" in {
    val str =
      """# main bit
        |
        |some content
        |
        |
        |## next bit
        |
        |more content
        |### third bit
        |third bit of content""".stripMargin

    val result1 = markdownLoader.getContent(str).head.asInstanceOf[ContentTag]
    result1.contents.length mustBe 2
    result1.contents(0) mustBe a[ContentString]
    result1.contents(1) mustBe a[ContentTag]
  }

  it should "find content with nested aliases" in {

    val str =
      """[this is an]<alias>
        |# header 1
        |[alias within]<the header>
        |some content""".stripMargin

    val results = markdownLoader.getContent(str)
    results.length mustBe 2
    val res1 = results(0)
    val res2 = results(1)

    res1 mustBe a [ContentTagAlias]
    val res1Alias = res1.asInstanceOf[ContentTagAlias]

    res1Alias.getAliasedQuery mustBe "alias"
    res2 mustBe a [ContentTag]

    val res2Tag = res2.asInstanceOf[ContentTag]
    val innerContent = res2Tag.contents

    innerContent(0) mustBe a [ContentTagAlias]
    val innerAlias = innerContent(0).asInstanceOf[ContentTagAlias]
    innerAlias.getAliasedQuery mustBe "header 1 the header"
    innerContent(1) mustBe a [ContentString]
  }

  "extractContentAliasesFromStrings" should "not change anything if there are no aliases" in {
    val cs1 = new ContentString(List(MdString("hello1", @@(0,1))), "", "")
    val cs2 = new ContentString(List(MdString("hello2", @@(1,2))), "", "")
    val cs3 = new ContentString(List(MdString("hello3", @@(2,3))), "", "")

    val tag = new ContentTag(List(cs1, cs2), MdHeader(MdString("tag", @@(0,0)),1, @@(0,0)), Nil,"")

    val contents = List(
      tag, cs3
    )

    markdownLoader.extractContentAliasesFromStrings(contents) mustBe contents
  }

  it should "split a string with aliases into a list of strings and aliases" in {
    val s1 = MdPlainString("hello", nowhere)
    val s2 = MdPlainString("hello2", nowhere)

    val pars = List(
      s1,
      MdAlias("alias","label;label2", nowhere),
      s2
    )

    markdownLoader.extractContentAliasesFromStrings(List(new ContentString(pars,""))) mustBe List(
      ContentString(List(s1), ""),
      ContentTagAlias("alias", List("label", "label2"), nowhere, ""),
      ContentString(List(s2), "")
    )
  }

}
