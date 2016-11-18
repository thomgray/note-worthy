package com.gray.markdown

abstract class MdParagraph

abstract class MdItem

abstract class MdList() extends MdParagraph{
  val items: List[MdListItem]
  private var _tier = 0
  protected [markdown] def setTier(int: Int): Unit ={_tier = int ; items.foreach(_.setTier(int))}
  def tier = _tier
}

abstract class MdListItem() extends MdItem {
  private var _tier = 0
  val paragraphs: List[MdParagraph]
  protected [markdown] def setTier(int: Int) = {
    _tier = int
    paragraphs.foreach {
      case innerList: MdList => innerList.setTier(int + 1)
      case _ =>
    }
  }
  def tier = _tier
}

case class MdString(string: String) extends MdParagraph {
  override def toString: String = string
}

///List
case class MdBulletList(override val items: List[MdBulletListItem]) extends MdList
case class MdBulletListItem(override val paragraphs: List[MdParagraph]) extends MdListItem

case class MdNumberList(override val items: List[MdNumberListItem]) extends MdList
case class MdNumberListItem(override val paragraphs: List[MdParagraph], number: Int) extends MdListItem

case class MdCheckList(override val items: List[MdCheckListItem]) extends MdList
case class MdCheckListItem(override val paragraphs: List[MdParagraph], checked: Boolean) extends MdListItem

/// block classes
case class MdQuoteBlock(string: String) extends MdParagraph

case class MdCodeBlock(string: String, syntax: Option[String] = None) extends MdParagraph

//single line classes
case class MdLiteral(string: String) extends MdParagraph

case class MdBreak() extends MdParagraph

case class MdHeader(string: String, size: Int) extends MdParagraph

//table
case class MdTableRow(cells: List[MdCell]) extends MdParagraph {
  def columnCount = cells.size
}

case class MdCell(string: MdString) extends MdItem

case class MdTable(rows: List[MdTableRow]) extends MdParagraph {
  def rowCount = rows.length

  def columnCount = rows.foldRight[Int](0) { (row, z) =>
    if (z >= row.columnCount) z else row.columnCount
  }
}

case class MdHorizontalLine() extends MdParagraph

case class MdLink(url: String, inlineString : Option[String] = None)
case class MdLinkRef(url: String, refText: String)



