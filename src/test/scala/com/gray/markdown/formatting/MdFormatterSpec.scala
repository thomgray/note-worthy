package com.gray.markdown.formatting

import com.gray.markdown._
import org.scalatest.{FlatSpec, MustMatchers}

class MdFormatterSpec extends FlatSpec with MustMatchers with MdCharacterConstants {
  val formatter = MdFormatter()

  "renderList[Bullet]" should "print linear properly" in {
    val item1 = MdBulletListItem(List(MdString("Hello")))
    val item2 = MdBulletListItem(List(MdString("There")))
    val item3 = MdBulletListItem(List(MdString("You")))
    val list = MdBulletList(List(item1, item2, item3))


    val expected =
      """   •  Hello
        |   •  There
        |   •  You""".stripMargin

    val actual = formatter.removeFormatting(formatter.renderList(list, 100))
    actual must equal(expected)
  }

  it should "print nested properly" in {
    val item3 = MdBulletListItem(List(MdString("You")))
    val nested3 = MdBulletList(List(item3))
    val item2 = MdBulletListItem(List(MdString("There"), nested3))
    val nested2 = MdBulletList(List(item2))

    val item1 = MdBulletListItem(List(MdString("Hello"), nested2))
    item1.setTier(0)
    val list = MdBulletList(List(item1))

    val expected = """   •  Hello
                     |         ◦  There
                     |               ⁃  You""".stripMargin

    val actual = formatter.removeFormatting(formatter.renderList(list, 100))
    actual mustBe expected
  }

  "lowercaseRoman" should "translate ints to romans up to an amout" in {
    lowercaseRoman(1) mustBe "i"
    lowercaseRoman(10) mustBe "x"
    lowercaseRoman(4) mustBe "iv"
    lowercaseRoman(100) mustBe "c"
    lowercaseRoman(5) mustBe "v"
    lowercaseRoman(18) mustBe "xviii"
    lowercaseRoman(53) mustBe "liii"
    lowercaseRoman(12) mustBe "xii"
    lowercaseRoman(99) mustBe "xcix"
  }

}
