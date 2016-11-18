package com.gray.markdown.parsing

import com.gray.markdown.formatting.MdFormatter
import com.gray.markdown._
import org.mockito.Matchers
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, MustMatchers}

class MdParserSpec extends FlatSpec with MustMatchers with MockitoSugar with BeforeAndAfter {

  val stubFactory = mock[DefaultMdFactory]

  class TestParser(string: String) extends MdParsing {
    override val factory: MdFactory = stubFactory
    override protected[parsing] val docString: String = string
  }

  val stubMdBulletList = MdBulletList(List(MdBulletListItem(List(MdString("")))))
  val stubMdNumberList = MdNumberList(List(MdNumberListItem(List(MdString("")), 1)))
  val stubMdCheckboxList = MdCheckList(List(MdCheckListItem(List(MdString("")), true)))
  val stubMdLiteral = MdLiteral("")
  val stubMdString = MdString("")
  val stubMdHeader = MdHeader("",0)
  val stubMdCodeBlock = MdCodeBlock("")
  val stubMdQuoteBlock = MdQuoteBlock("")

  val stubMdTable = MdTable(List(MdTableRow(List(MdCell(MdString(""))))))

  before {
    reset(stubFactory)
  }

  def parse(string: String) = {
    val parser = new TestParser(string)
    parser.parse()
    parser.paragraphs.toList.filter {
      case MdBreak() => false
      case _ => true
    }
  }

  "parse" should "parse header" in {
    when(stubFactory.makeMdHeader("#### header")) thenReturn stubMdHeader
    when(stubFactory.makeMdHeader("### header")) thenReturn stubMdHeader
    val str =
      """#### header
        |### header""".stripMargin
    val results = parse(str)
    verify(stubFactory).makeMdHeader("#### header")
    verify(stubFactory).makeMdHeader("### header")
    verifyNoMoreInteractions(stubFactory)
  }

  it should "parse empty lines in between" in {
    when(stubFactory.makeMdHeader("#### header")) thenReturn stubMdHeader
    when(stubFactory.makeMdHeader("### header")) thenReturn stubMdHeader
    val str =
      """
        |#### header
        |
        |### header
        | """.stripMargin
    val results = parse(str)
    verify(stubFactory).makeMdHeader("#### header")
    verify(stubFactory).makeMdHeader("### header")
    verifyNoMoreInteractions(stubFactory)
  }

  it should "parse code blocks and literal indents" in {
    when(stubFactory.makeMdHeader("#### header")) thenReturn stubMdHeader
    when(stubFactory.makeMdIndentedLiteral(List("    this is", "    literal"))) thenReturn stubMdLiteral
    when(stubFactory.makeMdCodeBlock(List("```", "this is a code block", "return something", "```"))) thenReturn stubMdCodeBlock
    val str =
      """#### header
        |
        |    this is
        |    literal
        |```
        |this is a code block
        |return something
        |```
        |
      """.stripMargin
    val results = parse(str)

    verify(stubFactory).makeMdHeader("#### header")
    verify(stubFactory).makeMdIndentedLiteral(List("    this is", "    literal"))
    verify(stubFactory).makeMdCodeBlock("```\nthis is a code block\nreturn something\n```".split("\n").toList)
  }

  it should "parse lists" in {
    val list1str = List(
      "list item 1",
      """2
        |- third
        |continuing on line
        |""".stripMargin
    )
    val list2str = List(
      """another list
        |     -this is literal part of the inner list item
        |""".stripMargin
    )

    val list3str = List(
      """[ ] checkbox
        |- inner bullet""".stripMargin,
      "[x] next checkbox\n"
    )

    when(stubFactory.makeMdBulletList(Matchers.any[List[String]]())) thenReturn stubMdBulletList
    when(stubFactory.makeMdNumberList(Matchers.any[List[String]]())) thenReturn stubMdNumberList
    when(stubFactory.makeMdCheckboxList(Matchers.any[List[String]]())) thenReturn stubMdCheckboxList
    when(stubFactory.makeMdIndentedLiteral(List("            -this is a separate literal"))) thenReturn MdLiteral("")

    val str =
      """
        |- list item 1
        |- 2
        |  - third
        |  continuing on line
        |
        |
        |1. another list
        |        -this is literal part of the inner list item
        |
        |
        |- [ ] checkbox
        |  - inner bullet
        |- [x] next checkbox
        |
        |
        |            -this is a separate literal
        |""".stripMargin

    val result = parse(str)

    verify(stubFactory).makeMdBulletList(list1str)
    verify(stubFactory).makeMdNumberList(list2str)
    verify(stubFactory).makeMdCheckboxList(list3str)
    verify(stubFactory).makeMdIndentedLiteral(List("            -this is a separate literal"))
    verifyNoMoreInteractions(stubFactory)
  }
}
