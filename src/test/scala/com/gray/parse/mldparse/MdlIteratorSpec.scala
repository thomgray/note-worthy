//package com.gray.parse.mldparse
//
//import com.gray.parse.ParseConstants
//import com.gray.parse.mdlparse._
//import com.gray.note.util.Ranj
//import org.scalatest.MustMatchers._
//import org.scalatest._
//import org.scalatest.exceptions.TestFailedException
//
//class MdlIteratorSpec extends FlatSpec with ParseConstants{
//
//    "getRangeOfNextBlock" should "find the range of the block starting from the beginning" in {
//      val lines =
//        """[[[ hello
//          |sdf
//          |sdfs
//          | [[[there
//          | sdfsadf
//          | sdfsdf
//          | ]]]
//          |]]]
//          |sdfsdf
//          |sdfs
//        """.stripMargin.split("\n")
//      val rng = MdlIterator.getRangeOfNextBlock(0, lines).get
//      rng.start mustBe 0
//      rng.end mustBe 8
//    }
//
//    it should "return a range starting from some way into the string" in {
//      val lines =
//        """[[[hello
//          |content
//          |content
//          |[[[label
//          |]]]
//          |]]]
//        """.stripMargin.split("\n")
//      val rng = MdlIterator.getRangeOfNextBlock(1, lines).get mustBe Ranj(3,5)
//    }
//
//    it should "not return a range if no block exists" in {
//      val lines =
//        """sdfsdf
//          |sdfffsdfgdsfhsfghddf
//        """.stripMargin.split("\n")
//      MdlIterator.getRangeOfNextBlock(0, lines) mustBe None
//    }
//
//    it should "not return a range if the next block doesn't close" in {
//      val lines =
//        """[[[hello
//          |[[[there
//          |]]]
//        """.stripMargin.split("\n")
//      MdlIterator.getRangeOfNextBlock(0, lines) mustBe None
//    }
//
//    "nextThing" should "get the first content tag" in {
//      val lines =
//        """
//          |[[[ label1;label2
//          |content
//          |content
//          |]]]
//          |[[[more stuff
//          |]]]
//        """.stripMargin.split("\n")
//      val it = MdlIterator
//      val firstTag = it.nextThingFrom(0, lines, 0).get
//      firstTag._1.string mustBe "content\ncontent"
//      firstTag._1.labels mustBe Some(List("label1", "label2"))
//      firstTag._1.options mustBe ""
//      firstTag._1.description mustBe CONTENT_TAG
//    }
//
//    it should "get the second content tag" in {
//      val lines =
//        """
//          |[[[ label1;label2
//          |content
//          |content
//          |]]]
//          |[[[more stuff
//          |]]]
//        """.stripMargin.split("\n")
//      val it = MdlIterator
//      val thing1 = it.nextThingFrom(0, lines, 0)
//      val firstTag = it.nextThingFrom(thing1.get._2, lines, 0).get
//      firstTag._1.string mustBe ""
//      firstTag._1.labels mustBe Some(List("more stuff"))
//      firstTag._1.options mustBe ""
//      firstTag._1.description mustBe CONTENT_TAG
//    }
//
//    it should "get the first content string" in {
//      val lines =
//        """some string
//          |[[[ label1;label2
//          |content
//          |content
//          |]]]
//          |[[[more stuff
//          |]]]
//        """.stripMargin.split("\n")
//      val it = MdlIterator
//      val firstTag = it.nextThingFrom(0, lines, 0).get
//      firstTag._1.string mustBe "some string"
//      firstTag._1.labels mustBe None
//      firstTag._1.options mustBe ""
//      firstTag._1.description mustBe CONTENT_STRING
//    }
//
//    it should "preserve indents" in {
//      val lines =
//        """
//          |   [[[ label1;label2
//          |     content
//          |     content
//          |   ]]]
//          |[[[more stuff
//          |]]]
//        """.stripMargin.split("\n")
//      val it = MdlIterator
//      val firstTag = it.nextThingFrom(0,lines,0).get
//      firstTag._1.string mustBe "  content\n  content"
//      firstTag._1.labels mustBe Some(List("label1", "label2"))
//      firstTag._1.options mustBe ""
//      firstTag._1.description mustBe CONTENT_TAG
//    }
//
//    it should "ignore empty whitespace between tags" in {
//      val lines =
//        """[[[tag0
//          |tag
//          |]]]
//          |
//          |[[[tag1
//          |]]]
//          |
//          |
//          |[[[tag2
//          |]]]
//          |
//        """.stripMargin.split("\n")
//      val it = MdlIterator
//      var marker = 0
//      for (i <- 0 until 3) {
//        val thing = it.nextThingFrom(marker, lines, 0).get
//        thing._1.description mustBe CONTENT_TAG
//        thing._1.labels.get.head mustBe s"tag$i"
//        marker = thing._2
//      }
//      it.nextThingFrom(marker, lines, 0).isEmpty mustBe true
//    }
//
//  it should "remove the indentation inline with the header tag" in {
//    val lines =
//      """   [[[tag0
//        |   body
//        |   ]]]
//        | [[[tag1
//        |body
//        |]]]
//        |   [[[tag2
//        |     body
//        |   ]]]""".stripMargin.split("\n")
//    val it = MdlIterator
//    var marker = 0
//    it.nextThingFrom(marker, lines, 0) match {
//      case Some(thing) => thing._1.string mustBe "body"
//        marker = thing._2
//      case None => fail
//    }
//    it.nextThingFrom(marker, lines, 0) match {
//      case Some(thing) => thing._1.string mustBe "body"
//        marker = thing._2
//      case None => fail
//    }
//    it.nextThingFrom(marker, lines, 0) match {
//      case Some(thing) => thing._1.string mustBe "  body"
//      case None => fail
//    }
//  }
//
//  it should "find the string within a tag result" in {
//    val lines =
//      """[[[tag
//        |
//        |string
//        |
//        |[[[inner tag
//        |inner string
//        |]]]
//        |
//        |string2
//        |]]]
//      """.stripMargin.split("\n")
//    val it = MdlIterator
//    val res = it.nextThingFrom(0, lines, 0)
//    res.get._1.string mustBe
//      """
//        |string
//        |
//        |[[[inner tag
//        |inner string
//        |]]]
//        |
//        |string2""".stripMargin
//  }
//}