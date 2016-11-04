package com.gray.markdown.formatting

import com.gray.markdown._
import org.scalatest.{FlatSpec, MustMatchers}

class MdFormatterSpec extends FlatSpec with MustMatchers with MdLiterals {
  val formatter = MdFormatter

  "list" should "print properly" in {
    val item1 = MdBulletedListItem(MdString("Hello"), 0)
    val item2 = MdBulletedListItem(MdString("There"), 1)
    val item3 = MdBulletedListItem(MdString("You"), 2)
    val list = MdList(List(item1, item2, item3))


    val expected = """• Hello
                     |   ◦ There
                     |      ⁃ You""".stripMargin
    formatter.renderList(list, 100) mustBe expected
  }

}
