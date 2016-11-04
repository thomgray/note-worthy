package com.gray.markdown.parsing

import com.gray.markdown.formatting.MdFormatter
import com.gray.markdown._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, MustMatchers}

class MdParserSpec extends FlatSpec with MustMatchers with MockitoSugar with BeforeAndAfter {

  val stubFactory = mock[DefaultMdFactory]

  class TestParser(string: String) extends MdParsing {
    override val factory: MdFactory = stubFactory
    override protected[parsing] val lines: List[String] = string.split("\n").toList
  }

  val stubMdList = MdList(List(MdBulletedListItem(MdString(""), 0)))
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

    val list1str =
      """- list item 1
        |- 2
        |  - third
        |  continuing on line""".stripMargin.split("\n").toList
    val list2str =
      """- another list
        |  - inner list item
        |            -this is literal part of the inner list item""".stripMargin.split("\n").toList

    val list1 = MdList(List(MdBulletedListItem(MdString("foo"), 0)))
    when(stubFactory.makeList(list1str)) thenReturn stubMdList
    when(stubFactory.makeList(list2str)) thenReturn stubMdList
    when(stubFactory.makeMdIndentedLiteral(List("            -this is a separate literal"))) thenReturn MdLiteral("")
    val str =
      """
        |- list item 1
        |- 2
        |  - third
        |  continuing on line
        |
        |- another list
        |  - inner list item
        |            -this is literal part of the inner list item
        |
        |            -this is a separate literal
        |""".stripMargin
    val result = parse(str)

    verify(stubFactory).makeList(list1str)
    verify(stubFactory).makeList(list2str)
    verify(stubFactory).makeMdIndentedLiteral(List("            -this is a separate literal"))
    verifyNoMoreInteractions(stubFactory)
  }
}
