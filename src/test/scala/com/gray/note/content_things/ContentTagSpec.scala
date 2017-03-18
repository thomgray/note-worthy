package com.gray.note.content_things

import com.gray.markdown.{@@, MdHeader, MdString}
import com.gray.parse.ParseConstants
import org.scalatest.{FlatSpec, MustMatchers}

class ContentTagSpec extends FlatSpec with MustMatchers with ParseConstants{

  val loader = MdlLoader
  val string1 = MdPlainString("string1", @@(0,0))
  it should "initialise properly" in {
    //contents not set - that is done by the content loader
    val tag = new ContentTag(
        List(new ContentString(List(string1), "")),
        MdHeader(MdString("hellotag", @@(0,1)), 1, @@(0,1)),
        Nil,
        ""
    )
    tag.isParaphrase mustBe false
    tag.getLabels mustBe List("hellotag")
  }


}
