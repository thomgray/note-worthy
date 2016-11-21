package com.gray.markdown.parsing

import com.gray.markdown._

trait MdFactory extends MdRegexes {

  def makeMdCodeBlock(lines: List[String]): MdCodeBlock

  def makeMdHeader(line: String): MdHeader

  def makeMdIndentedLiteral(lines: List[String]): MdLiteral

  def makeMdQuoteBlock(lines: List[String]): MdQuoteBlock

  def makeMdBulletList(items: List[String]): MdBulletList

  def makeMdCheckboxList(items: List[String]): MdCheckList

  def makeMdNumberList(items: List[String]): MdNumberList

  def makeMdString(lines: List[String]): MdString
}


class DefaultMdFactory extends MdFactory {
  override def makeMdCodeBlock(lines: List[String]) = {
    val newLines = lines.slice(1, lines.length - 1)
    val syntax = "\\w+".r.findFirstIn(lines(0))
    val str = newLines.mkString("\n")
    MdCodeBlock(str, syntax)
  }

  override def makeMdHeader(line: String) = {
    val prefix = "#+".r.findFirstIn(line).get
    val cleaned = line.replaceAll("^ *#+ *", "").replaceAll(" *#+ *$", "")
    MdHeader(cleaned, prefix.length)
  }

  override def makeMdIndentedLiteral(lines: List[String]) = {
    val string = lines.map(line => line.substring(4)).mkString("\n")
    MdLiteral(string)
  }

  override def makeMdQuoteBlock(lines: List[String]) = {
    val lines1 = lines.map(l => l.stripPrefix("^ *\\> *".r.findFirstIn(l).getOrElse("")))
    val lines2 = makeMdString(lines1).string
    MdQuoteBlock(lines2)
  }

  ///List making

  override def makeMdBulletList(items: List[String]): MdBulletList = {
    val bulletListItems = items.map(str => MdBulletListItem(MdParser.parse(str)))
    val list = MdBulletList(bulletListItems)
    list.setTier(list.tier) // looks redundant, but triggers recursive tier setting for sub-items
    list
  }

  override def makeMdCheckboxList(items: List[String]) = {
    val checkListItems = items.map { str =>
      val prefix = " *\\[( |x)\\] *".r.findFirstIn(str).getOrElse("")
      val rest = str.stripPrefix(prefix)
      MdCheckListItem(MdParser.parse(rest), prefix.contains("[x]"))
    }
    val list = MdCheckList(checkListItems)
    list.setTier(list.tier) // looks redundant, but triggers recursive tier setting for sub-items
    list
  }

  override def makeMdNumberList(items: List[String]) = {
    val indices = items.indices
    val listItems = items.indices.map(i => MdNumberListItem(MdParser.parse(items(i)), i + 1)).toList
    val list = MdNumberList(listItems)
    list.setTier(0) // looks redundant, but triggers recursive tier setting for sub-items
    list
  }

  //String making

  //todo add link analysis and inline formatting here
  override def makeMdString(lines: List[String]) = {
    val string = lines.map { line =>
      if (matchString(" {2,}$".r, line)) line.replaceAll(" +", " ").trim + "\n"
      else line.replaceAll(" +", " ").trim
    }.mkString(" ").replaceAll("\n ", "\n")
    MdString(string)
  }
}
