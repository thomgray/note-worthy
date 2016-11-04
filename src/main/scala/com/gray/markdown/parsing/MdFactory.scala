package com.gray.markdown.parsing

import com.gray.markdown._

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait MdFactory extends MdRegexes {
  def makeMdCodeBlock(lines: List[String]) = {
    val newLines = lines.slice(1, lines.length - 1)
    val syntax = "\\w+".r.findFirstIn(lines(0))
    val str = newLines.mkString("\n")
    MdCodeBlock(str, syntax)
  }

  def makeMdHeader(line: String) = {
    val prefix = "#+".r.findFirstIn(line).get
    val cleaned = line.replaceAll("^ *#+ *", "").replaceAll(" *#+ *$", "")
    MdHeader(cleaned, prefix.length)
  }

  def makeMdIndentedLiteral(lines: List[String]) = {
    val string = lines.map(line => line.substring(4)).mkString("\n")
    MdLiteral(string)
  }

  def makeMdQuoteBlock(lines: List[String]) = {
    MdQuoteBlock(lines.mkString("\n"))
  }

  ///List making

  def makeList(lines: List[String]) = {
    val lineResults = packageLinesIntoResults(lines)
    val items = getItemsFromResults(lineResults)
    MdList(items.toList)
  }

  private def packageLinesIntoResults(lines: List[String]) = {
    val resultBuffer = new mutable.MutableList[ListItemResult]
    var previousPrefixLength = 0
    for (line <- lines) {
      val indent = leadingWhitespaceRegex.findFirstIn(line).get.length
      listItemPrefixRegex.findFirstIn(line) match {
        case Some(prefix) if indent < previousPrefixLength + 4 => // a new item
          val newItem = ListItemResult(indent, prefix.length, prefix)
          newItem.lines += line.stripPrefix(prefix)
          resultBuffer += newItem
          previousPrefixLength = newItem.prefixIndent
        case _ => //this is not a list item
          resultBuffer.last.lines += line
      }
    }
    resultBuffer.toList
  }

  private def getItemsFromResults(results: List[ListItemResult]) = {
    val outBuffer = new ListBuffer[(ListItemResult, MdListItem)]()

    def findTierForIndent(indent: Int) = outBuffer.reverse.foldLeft(-1) { (tier, tuple) =>
      val (result, item) = (tuple._1, tuple._2)
      tier match {
        case -1 =>
          if (indent >= result.indent && indent < result.prefixIndent) item.tier
          else if (indent >= result.prefixIndent) item.tier + 1
          else -1
        case other => other
      }
    } match {
      case -1 => 0
      case other => other
    }

    for (result <- results) {
      outBuffer += (result -> makeListItem(result, findTierForIndent(result.indent)))
    }
    outBuffer.map(_._2)
  }

  private def makeListItem(listItemResult: ListItemResult, tier: Int) = {
    val string = makeMdString(listItemResult.lines.toList)
    val prefix = listItemResult.prefix.trim
    if (matchString("\\[(x| )\\]$".r, prefix)) {
      val checked = matchString("x".r, prefix)
      MdTaskListItem(string, tier, checked)
    } else if (matchString("^(-|\\+|\\*)".r, prefix)) {
      MdBulletedListItem(string, tier)
    } else if (matchString("".r, prefix)) {
      val number = "\\d+".r.findFirstIn(prefix).get.toInt
      MdNumberedListItem(string, tier, number)
    } else throw new Exception
  }

  private case class ListItemResult(indent: Int, prefixIndent: Int, prefix: String) {
    val lines: mutable.MutableList[String] = new mutable.MutableList[String]()
  }

  //String making

  def makeMdString(lines: List[String]) = {
    val string = lines.map { line =>
      if (matchString(" {2,}$".r, line)) line.replaceAll(" +", " ").trim + "\n"
      else line.replaceAll(" +", " ").trim
    }.mkString(" ").replaceAll("\n ", "\n")
    MdString(string)
  }


}

class DefaultMdFactory extends MdFactory
