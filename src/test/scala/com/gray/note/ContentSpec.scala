package com.gray.note

import com.gray.note.content_things.{ContentString, ContentTag}
import org.scalatest.MustMatchers._

class ContentSpec extends BaseSpec {

  val testTagString =
    """{@(tag1){
      |tag1 content 1
      |@(tag2){tag2 content 1}
      |@(tag3){tag3 content 1
      |@(tag4){tag4 content 1}
      |tag3 content 2}
      |tag1 content 2
      |}}
    """.stripMargin

  "Content" should "return all descendant content" in {
    val t = new ContentTag(testTagString, "")
    assertResult(1)(t.contents.length)
    val allContent = t.getAllDescendantContent
    allContent.length mustBe 11
    allContent(1).asInstanceOf[ContentTag].labels.get(0) mustBe "tag1"
    allContent(3).asInstanceOf[ContentTag].labels.get(0) mustBe "tag2"
    allContent(4).asInstanceOf[ContentString].toString mustBe "tag2 content 1"
    allContent.last.asInstanceOf[ContentString].toString mustBe "tag1 content 2"
  }

}
