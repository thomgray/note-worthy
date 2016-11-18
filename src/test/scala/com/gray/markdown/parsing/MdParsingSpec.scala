package com.gray.markdown.parsing

import com.gray.markdown._
import org.mockito.Matchers._
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, FlatSpec, MustMatchers}

class MdParsingSpec extends FlatSpec with MustMatchers  with MockitoSugar with BeforeAndAfter with MdRegexes {
  val stubBulletList = MdBulletList(List(MdBulletListItem(List(MdString("")))))
  val stubNumberList = MdNumberList(List(MdNumberListItem(List(MdString("")), 1)))
  val stubCheckList = MdCheckList(List(MdCheckListItem(List(MdString("")), true)))
  val stubMdLiteral = MdLiteral("")
  val stubMdString = MdString("")
  val stubMdHeader = MdHeader("",0)
  val stubMdCodeBlock = MdCodeBlock("")
  val stubMdQuoteBlock = MdQuoteBlock("")

  val stubMdTable = MdTable(List(MdTableRow(List(MdCell(MdString(""))))))

  val mockFactory = mock[MdFactory]

  def testParser(string: String, from: Int = 0) = new MdParsing {
    marker = from
    override val factory: MdFactory = mockFactory
    override protected[parsing] val docString: String = string
  }

  before {
    reset(mockFactory)
  }

  "checkBulletListItem" should "find the next bullet list item" in {
    val str =
      """sknkjnldkfnsdf
        |
        |- this is a bullet
        |- this is a different bullet""".stripMargin
    testParser(str, 2).checkListItem(bulletListItemRegex) mustBe Some("this is a bullet")
    testParser(str, 3).checkListItem(bulletListItemRegex) mustBe Some("this is a different bullet")
  }

  "checkBulletList" should "find linear bullet lists" in {
    when(mockFactory.makeMdBulletList(anyObject[List[String]]())) thenReturn stubBulletList
    val str =
      """- helo
        |- there
        |- you
        |
        |bleeding hell
        |""".stripMargin
    val parser = testParser(str)
    parser.checkBulletList().get
    parser.marker mustBe 4

    verify(mockFactory, times(1)).makeMdBulletList(List("helo", "there", "you\n"))
    verifyNoMoreInteractions(mockFactory)
  }

  it should "find nested bullet lists" in {
    val str =
      """hello there
        |
        |- one
        |  - two
        |    - three
        |
        |diddledidee
        |
      """.stripMargin
    when(mockFactory.makeMdBulletList(anyObject[List[String]]())) thenReturn stubBulletList
    val parser = testParser(str, 2)
    parser.checkBulletList()
    parser.marker mustBe 6

    verify(mockFactory).makeMdBulletList(List("one\n- two\n  - three\n"))
  }

  // Could ensure this, but would be simpler to make sure that checkList items are checked before bullet list items!
  it should "not find a bullet list for a checkbox list" in {
    val str =
      """hello there
        |
        |- [ ] one
        |  - two
        |    - three
        |
        |diddledidee
        |
      """.stripMargin
    when(mockFactory.makeMdBulletList(anyObject[List[String]]())) thenReturn stubBulletList
    val parser = testParser(str, 2)
    parser.checkBulletList()
    parser.marker mustBe 2

    verifyNoMoreInteractions(mockFactory)
  }

  "checkCheckBox" should "find a checkbox" in {
    val str =
      """
        |- [x] this is a checkbox
        |- [ ] this is another
        |this is not
      """.stripMargin

    testParser(str, 1).checkListItem(checkListItemRegex) mustBe Some("[x] this is a checkbox")
    testParser(str, 2).checkListItem(checkListItemRegex) mustBe Some("[ ] this is another")
  }

  it should "find a checkbox with continuous content" in {
    val str=
    """
      |- [x] this is a checkbox
      |  that continues on the next line
      |- [ ] this is another
      |this is not
    """.stripMargin

    testParser(str, 1).checkListItem(checkListItemRegex) mustBe Some("[x] this is a checkbox\nthat continues on the next line")
  }

  it should "find a checkbox with continous code content" in {
    val str = """+ [ ] include a rule that ignored markdown formatting, i.e. keeps a block literal. something like:
                |  ```code
                |  this
                |  so  I     can be   liberal  with the format    !
                |  ```""".stripMargin

    val parser = testParser(str)
    parser.checkListItem(checkListItemRegex) mustBe Some("""[ ] include a rule that ignored markdown formatting, i.e. keeps a block literal. something like:
                                                                |```code
                                                                |this
                                                                |so  I     can be   liberal  with the format    !
                                                                |```""".stripMargin)
  }

  it should "find a checkbox with continous code content with single blank lines" in {
    val str = """+ [ ] include a rule that ignored markdown formatting, i.e. keeps a block literal. something like:
                |
                |  ```code
                |  this
                |  so  I     can be   liberal  with the format    !
                |  ```""".stripMargin

    val parser = testParser(str)
    parser.checkListItem(checkListItemRegex) mustBe Some("""[ ] include a rule that ignored markdown formatting, i.e. keeps a block literal. something like:
                                                                |
                                                                |```code
                                                                |this
                                                                |so  I     can be   liberal  with the format    !
                                                                |```""".stripMargin)
  }

  "checkList()" should "find separate types of list when written compact" in {
    val str =
      """
        |- bullet
        |* bullet2
        |1. num1
        |2. num2
        |- [ ] check1
        |- [x] check2""".stripMargin
    val parser = testParser(str, 1)
    when(mockFactory.makeMdBulletList(any[List[String]]())) thenReturn stubBulletList
    when(mockFactory.makeMdNumberList(any[List[String]]())) thenReturn stubNumberList
    when(mockFactory.makeMdCheckboxList(any[List[String]]())) thenReturn stubCheckList

    parser.checkList() mustBe Some(stubBulletList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubNumberList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubCheckList)

    verify(mockFactory, times(1)).makeMdBulletList(List("bullet", "bullet2"))
    verify(mockFactory, times(1)).makeMdNumberList(List("num1", "num2"))
    verify(mockFactory, times(1)).makeMdCheckboxList(List("[ ] check1", "[x] check2"))
    verifyNoMoreInteractions(mockFactory)
  }

  it should "find separate types of list when written separated by one line" in {
    val str =
      """
        |- bullet
        |* bullet2
        |
        |1. num1
        |2. num2
        |
        |- [ ] check1
        |- [x] check2
        |
        |""".stripMargin
    val parser = testParser(str, 1)
    when(mockFactory.makeMdBulletList(any[List[String]]())) thenReturn stubBulletList
    when(mockFactory.makeMdNumberList(any[List[String]]())) thenReturn stubNumberList
    when(mockFactory.makeMdCheckboxList(any[List[String]]())) thenReturn stubCheckList

    parser.checkList() mustBe Some(stubBulletList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubNumberList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubCheckList)

    verify(mockFactory, times(1)).makeMdBulletList(List("bullet", "bullet2\n"))
    verify(mockFactory, times(1)).makeMdNumberList(List("num1", "num2\n"))
    verify(mockFactory, times(1)).makeMdCheckboxList(List("[ ] check1", "[x] check2"))
    verifyNoMoreInteractions(mockFactory)
  }

  it should "find separate types of list when written separated by one line with nested content" in {
    val str =
      """
        |- bullet
        |* bullet2
        |  - inner1
        |  more content
        |1. num1
        |2. num2
        |
        |- [ ] check1
        |- [x] check2
        |
        |""".stripMargin
    val parser = testParser(str, 1)
    when(mockFactory.makeMdBulletList(any[List[String]]())) thenReturn stubBulletList
    when(mockFactory.makeMdNumberList(any[List[String]]())) thenReturn stubNumberList
    when(mockFactory.makeMdCheckboxList(any[List[String]]())) thenReturn stubCheckList

    parser.checkList() mustBe Some(stubBulletList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubNumberList)
    parser.marker += 1
    parser.checkList() mustBe Some(stubCheckList)

    verify(mockFactory, times(1)).makeMdBulletList(List("bullet", "bullet2\n- inner1\nmore content"))
    verify(mockFactory, times(1)).makeMdNumberList(List("num1", "num2\n"))
    verify(mockFactory, times(1)).makeMdCheckboxList(List("[ ] check1", "[x] check2"))
    verifyNoMoreInteractions(mockFactory)
  }

  "initLines()" should "find any reference links in the document" in {
    val str =
      """this is a but of text with [a reference][foo]
        |
        |[foo]: www.google.com
      """.stripMargin

    val parser = testParser(str)
    parser.lines.contains("[foo]: www.google.com") mustBe false
    parser.linkRefs mustBe Map("foo" -> "www.google.com")
  }

}
