package com.gray.parse.mldparse

import com.gray.parse.{ParseConstants, Range}
import com.gray.parse.mdlparse._
import org.scalatest._
import org.scalatest.MustMatchers._

class MldIteratorSpec extends FlatSpec with ParseConstants{

    "MdlIterator" should "split text into lines" in {
      val str = """hello there
                  |this is a
                  |few lines
                  |innit
                  |
                """.stripMargin
      val it = new MdlIterator(str)
      it.lines.length mustBe 4
    }

    it should "preserve indentation" in {
      val str =
        """
          | hello
          |   there
          |     how
          |are you?
        """.stripMargin
      val lines = MdlIterator(str).lines
      lines.length mustBe 4
      lines(0) mustBe " hello"
      lines(1) mustBe "   there"
      lines(2) mustBe "     how"
      lines(3) mustBe "are you?"
    }

    "getRangeOfNextBlock" should "find the range of the block starting from the beginning" in {
      val str =
        """[[[ hello
          |sdf
          |sdfs
          | [[[there
          | sdfsadf
          | sdfsdf
          | ]]]
          |]]]
          |sdfsdf
          |sdfs
        """.stripMargin
      val rng = new MdlIterator(str).getRangeOfNextBlock(0).get
      rng.start mustBe 0
      rng.end mustBe 8
    }

    it should "return a range starting from some way into the string" in {
      val str =
        """[[[hello
          |content
          |content
          |[[[label
          |]]]
          |]]]
        """.stripMargin
      val it = MdlIterator(str)
      val rng = MdlIterator(str).getRangeOfNextBlock(1).get mustBe Range(3,5)
    }

    it should "not return a range if no block exists" in {
      val str =
        """sdfsdf
          |sdfffsdfgdsfhsfghddf
        """.stripMargin
      val rng = MdlIterator(str).getRangeOfNextBlock(0) mustBe None
    }

    it should "not return a range if the next block doesn't close" in {
      val str =
        """[[[hello
          |[[[there
          |]]]
        """.stripMargin
      val rng = MdlIterator(str).getRangeOfNextBlock(0) mustBe None
    }

    "nextThing" should "get the first content tag" in {
      val str =
        """
          |[[[ label1;label2
          |content
          |content
          |]]]
          |[[[more stuff
          |]]]
        """.stripMargin
      val it = MdlIterator(str)
      val firstTag = it.nextThing.get
      firstTag.string mustBe "content\ncontent"
      firstTag.labels mustBe Some(List("label1", "label2"))
      firstTag.options mustBe ""
      firstTag.description mustBe CONTENT_TAG
    }

    it should "get the second content tag" in {
      val str =
        """
          |[[[ label1;label2
          |content
          |content
          |]]]
          |[[[more stuff
          |]]]
        """.stripMargin
      val it = MdlIterator(str)
      it.nextThing
      val firstTag = it.nextThing.get
      firstTag.string mustBe ""
      firstTag.labels mustBe Some(List("more stuff"))
      firstTag.options mustBe ""
      firstTag.description mustBe CONTENT_TAG
    }

    it should "get the first content string" in {
      val str =
        """some string
          |[[[ label1;label2
          |content
          |content
          |]]]
          |[[[more stuff
          |]]]
        """.stripMargin
      val it = MdlIterator(str)
      val firstTag = it.nextThing.get
      firstTag.string mustBe "some string"
      firstTag.labels mustBe None
      firstTag.options mustBe ""
      firstTag.description mustBe CONTENT_STRING
    }

    it should "preserve indents" in {
      val str =
        """
          |   [[[ label1;label2
          |     content
          |     content
          |   ]]]
          |[[[more stuff
          |]]]
        """.stripMargin
      val it = MdlIterator(str)
      val firstTag = it.nextThing.get
      firstTag.string mustBe "  content\n  content"
      firstTag.labels mustBe Some(List("label1", "label2"))
      firstTag.options mustBe ""
      firstTag.description mustBe CONTENT_TAG
    }

    it should "ignore empty whitespace between tags" in {
      val str =
        """[[[tag0
          |tag
          |]]]
          |
          |[[[tag1
          |]]]
          |
          |
          |[[[tag2
          |]]]
          |
        """.stripMargin
      val it = MdlIterator(str)

      for (i <- 0 until 3) {
        val thing = it.nextThing.get
        thing.description mustBe CONTENT_TAG
        thing.labels.get.head mustBe s"tag$i"
      }
      it.nextThing.isEmpty mustBe true
    }

  it should "remove the indentation inline with the header tag" in {
    val str =
      """   [[[tag0
        |   body
        |   ]]]
        | [[[tag1
        |body
        |]]]
        |   [[[tag2
        |     body
        |   ]]]""".stripMargin
    val it = MdlIterator(str)
    it.nextThing match {
      case Some(thing) => thing.string mustBe "body"
    }
    it.nextThing match {
      case Some(thing) => thing.string mustBe "body"
    }
    it.nextThing match {
      case Some(thing) => thing.string mustBe "  body"
    }
  }

  it should "find the string within a tag result" in {
    val str =
      """[[[tag
        |
        |string
        |
        |[[[inner tag
        |inner string
        |]]]
        |
        |string2
        |]]]
      """.stripMargin
    val it = MdlIterator(str)
    val res = it.nextThing
    res.get.string mustBe
      """
        |string
        |
        |[[[inner tag
        |inner string
        |]]]
        |
        |string2""".stripMargin
  }
}