package com.gray.markdown

abstract class MdParagraph
abstract class MdItem
abstract class MdListItem(_tier: Int, _string: MdString) extends MdItem {
  val tier = _tier
  val string = _string
}

case class MdString(string: String) extends MdParagraph {
  override def toString: String = string
}

///List
case class MdList(items: List[MdListItem]) extends MdParagraph
case class MdBulletedListItem(override val string: MdString, override val tier: Int) extends MdListItem(tier, string)
case class MdNumberedListItem(override val string: MdString, override val tier: Int, number: Int) extends MdListItem(tier, string)
case class MdTaskListItem(override val string: MdString, override val tier: Int, checked: Boolean) extends MdListItem(tier, string)

/// block classes
case class MdQuoteBlock(string: String) extends MdParagraph
case class MdCodeBlock(string: String, syntax: Option[String] = None) extends MdParagraph

//single line classes
case class MdLiteral(string: String) extends MdParagraph
case class MdBreak() extends MdParagraph
case class MdHeader(string: String, size: Int) extends MdParagraph

//table
case class MdTableRow(cells: List[MdCell]) extends MdParagraph{
  def columnCount = cells.size
}
case class MdCell(string: MdString) extends MdItem
case class MdTable(rows: List[MdTableRow]) extends MdParagraph {
  def rowCount = rows.length
  def columnCount = rows.foldRight[Int](0){ (row, z) =>
    if (z >= row.columnCount) z else row.columnCount
  }
}

//range
case class Range(start: Int, end: Int)
