package com.gray.note.content_things

import com.gray.markdown.{@@, MdHeader, MdString}
import com.gray.parse.{ParseConstants, ParseResult}
import org.scalatest.{FlatSpec, MustMatchers}

class ContentTagSpec extends FlatSpec with MustMatchers with ParseConstants{

  val loader = MdlLoader
  "ContentTag" should "initialise properly" in {
    //contents not set - that is done by the content loader
    val tag = new ContentTag(
        List(new ContentString(Nil, "", @@(0,0))),
        MdHeader(MdString("hellotag", @@(0,1)), 1, @@(0,1)),
        Nil,
        @@(0,2)
    )
    tag.isParaphrase mustBe false
    tag.getLabels mustBe List("hellotag")
  }


}
