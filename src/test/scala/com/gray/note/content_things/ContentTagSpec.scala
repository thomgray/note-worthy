package com.gray.note.content_things

import com.gray.parse.{ParseConstants, ParseResult}
import org.scalatest.{FlatSpec, MustMatchers}

class ContentTagSpec extends FlatSpec with MustMatchers with ParseConstants{

  val loader = MdlLoader
  "ContentTag" should "initialise properly" in {
    //contents not set - that is done by the content loader
    val tag = new ContentTag(ParseResult("hello", Some(List("hellotag")), CONTENT_TAG, ""))
    tag.isParaphrase mustBe false
    tag.getLabels mustBe List("hellotag")
  }


}
