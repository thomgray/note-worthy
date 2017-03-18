package com.gray.note.util.attributed_string

import com.gray.note.util.Ranj
import org.scalatest.{FlatSpec, MustMatchers}

import scala.io.AnsiColor

class AttributeSpec extends FlatSpec with MustMatchers {

  "copyInRange()" should "yield 0,4 copying 3,10 in range 6,12" in {
    val at1 = Attribute(Ranj(3,10), Seq("FOO"))
    val inRng = at1.copyInRange(Ranj(6,12))

    inRng mustBe Some(Attribute(Ranj(0,4), Seq("FOO")))
  }

  it should "yield None copying 3,7 in range 7,12" in {
    val at1 = Attribute(Ranj(3,10), Seq("FOO"))
    val inRng = at1.copyInRange(Ranj(6,12))

    inRng mustBe Some(Attribute(Ranj(0,4), Seq("FOO")))
  }

  "merge()" should "return 3 attributes when merged with a different overlapping attribute" in {
    val attribute = Attribute(Ranj(0,10), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(5,15), Seq(AnsiColor.CYAN_B))

    val merge = attribute.merge(anotherAttribute).get
    merge.length mustBe 3
    merge(0).range mustBe Ranj(0,5)
    merge(0).formats.toList mustBe List(AnsiColor.RED)
    merge(1).range mustBe Ranj(5,10)
    merge(1).formats.toList mustBe List(AnsiColor.RED, AnsiColor.CYAN_B)
    merge(2).range mustBe Ranj(10,15)
    merge(2).formats.toList mustBe List(AnsiColor.CYAN_B)
  }

  it should "return none if the attributes do not overlap" in {
    val attribute = Attribute(Ranj(1,11), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(15,16), Seq(AnsiColor.CYAN_B))
    attribute.merge(anotherAttribute) mustBe None
  }

  it should "return 3 attributes when merged with a different attribute within the head attrbute" in {
    val attribute = Attribute(Ranj(0,20), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(5,15), Seq(AnsiColor.CYAN_B))

    val merge = attribute.merge(anotherAttribute).get
    merge.length mustBe 3
    merge(0).range mustBe Ranj(0,5)
    merge(0).formats.toList mustBe List(AnsiColor.RED)
    merge(1).range mustBe Ranj(5,15)
    merge(1).formats.toList mustBe List(AnsiColor.RED, AnsiColor.CYAN_B)
    merge(2).range mustBe Ranj(15,20)
    merge(2).formats.toList mustBe List(AnsiColor.RED)
  }

  it should "return 2 attributes when merged with an attribute at the end of the head attribute" in {
    val attribute = Attribute(Ranj(0,20), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(5,20), Seq(AnsiColor.CYAN_B))

    val merge = attribute.merge(anotherAttribute).get
    merge.length mustBe 2
    merge(0).range mustBe Ranj(0,5)
    merge(0).formats.toList mustBe List(AnsiColor.RED)
    merge(1).range mustBe Ranj(5,20)
    merge(1).formats.toList mustBe List(AnsiColor.RED, AnsiColor.CYAN_B)
  }

  it should "return 2 attributes when merged with an attribute at the beginning of the head attribute" in {
    val attribute = Attribute(Ranj(0,20), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(0,15), Seq(AnsiColor.CYAN_B))

    val merge = attribute.merge(anotherAttribute).get
    merge.length mustBe 2
    merge(0).range mustBe Ranj(0,15)
    merge(0).formats.toList mustBe List(AnsiColor.RED, AnsiColor.CYAN_B)
    merge(1).range mustBe Ranj(15,20)
    merge(1).formats.toList mustBe List(AnsiColor.RED)
  }

  it should "override the head attribute with a conflicting new attribute" in {
    val attribute = Attribute(Ranj(0,20), Seq(AnsiColor.RED))
    val anotherAttribute = Attribute(Ranj(0,15), Seq(AnsiColor.CYAN))
    val merge = attribute.merge(anotherAttribute).get

    merge(0).formatString mustBe AnsiColor.CYAN
    merge(0).range mustBe Ranj(0,15)
    merge(1).formatString mustBe AnsiColor.RED
    merge(1).range mustBe Ranj(15,20)
  }


}
