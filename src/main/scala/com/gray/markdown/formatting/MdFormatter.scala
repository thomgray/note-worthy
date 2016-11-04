package com.gray.markdown.formatting

import com.gray.markdown._
import com.gray.util.DefaultFormatter

object MdFormatter extends DefaultFormatter with MdLiterals {

  def renderParagraph(paragraph: MdParagraph, width: Int) = paragraph match {
    case list: MdList => renderList(list, width)
    case other => other.toString
  }

  def renderList(list: MdList, width: Int)= {
    var str = ""
    for (item <- list.items) {
      val indent = concatenate(" ", item.tier*3)
      item match {
        case MdTaskListItem(string, tier, checked) =>
          val box = if (checked) BOX_CHECKED else BOX_UNCHECKED
          str += "\n" + indent + box + " " + string.toString
        case MdBulletedListItem(string, tier) =>
          val bullet = tier match {
            case 0 => BULLET1
            case 1 => BULLET2
            case 2 => BULLET3
            case _ => BULLET4
          }
          str += "\n" + indent + bullet + " " + string.toString
        case MdNumberedListItem(string, tier, number) =>
          str += "\n" + indent + number + ". " + string.toString
      }
    }
    str.substring(1)
  }

}

trait MdLiterals {
  val BULLET1 = "•"
  val BULLET2 = "◦"
  val BULLET3, DASH = "⁃"
  val BULLET4 = "∙"

  val BOX_CHECKED = "☒"
  val BOX_UNCHECKED = "☐"

  def H_LINE(width: Int) = (for (_ <- 0 until width) yield "─").mkString
}