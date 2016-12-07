package com.gray.markdown

import com.gray.util.Ranj
import sys.process._


trait MdIndexed {
  var index = 0
}

trait MdItemised {
  val items: List[MdParagraph]
}

abstract class MdParagraph

abstract class MdItem

abstract class MdList() extends MdParagraph {
  val items: List[MdListItem]
  private var _tier = 0

  items.foreach(_.setTier(_tier))

  protected[markdown] def setTier(int: Int): Unit = {
    _tier = int; items.foreach(_.setTier(int))
  }

  def tier = _tier
}

abstract class MdListItem() extends MdItem with MdItemised {
  private var _tier = 0
//  val items: List[MdParagraph]

  protected[markdown] def setTier(int: Int) = {
    _tier = int
    items.foreach {
      case innerList: MdList => innerList.setTier(int + 1)
      case _ =>
    }
  }

  def tier = _tier
}

case class MdString(string: String) extends MdParagraph {
  override def toString: String = string

  var links: List[(MdLink, Ranj)] = List.empty

  def inlineLinks = links.map(_._1)
}

case class MdPlainString(string: String) extends MdParagraph

///List
case class MdBulletList(override val items: List[MdBulletListItem]) extends MdList {
  setTier(0)
}

case class MdBulletListItem(override val items: List[MdParagraph]) extends MdListItem

case class MdNumberList(override val items: List[MdNumberListItem]) extends MdList {
  setTier(0)
}

case class MdNumberListItem(override val items: List[MdParagraph], number: Int) extends MdListItem

case class MdCheckList(override val items: List[MdCheckListItem]) extends MdList {
  setTier(0)
}

case class MdCheckListItem(override val items: List[MdParagraph], checked: Boolean) extends MdListItem with MdIndexed

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

case class MdLink(url: String, inlineString: Option[String] = None) extends MdIndexed {
  def open = if (url.startsWith("http")) {
    s"open $url".!
  }else{
    s"open https://$url".!
  }

}

case class MdLinkRef(url: String, refText: String)



