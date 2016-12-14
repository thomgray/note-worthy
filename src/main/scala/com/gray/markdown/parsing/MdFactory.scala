package com.gray.markdown.parsing

import com.gray.markdown._
import com.gray.util.{Formatting, Ranj}

import scala.io.AnsiColor

trait MdFactory extends MdRegexes with AnsiColor with Formatting {

  def makeMdCodeBlock(lines: List[String]): MdCodeBlock

  def makeMdHeader(line: String): MdHeader

  def makeMdIndentedLiteral(lines: List[String]): MdLiteral

  def makeMdQuoteBlock(lines: List[String]): MdQuoteBlock

  def makeMdBulletList(items: List[String]): MdBulletList

  def makeMdCheckboxList(items: List[String]): MdCheckList

  def makeMdNumberList(items: List[String]): MdNumberList

  def makeMdString(lines: List[String], linkRefs: List[MdLinkRef]): MdString
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
    val string = lines.map(line => line.stripPrefix("    ")).mkString("\n")
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

  override def makeMdString(lines: List[String], refs: List[MdLinkRef]= List.empty) = {
    val string = lines.map { line =>
      if (matchString(" {2,}$".r, line)) line.replaceAll(" +", " ").trim + "\n"
      else line.replaceAll(" +", " ").trim
    }.mkString(" ").replaceAll("\n ", "\n")

    val outString = MdString(formatString(string))
    outString.links = getLinks(outString.string, refs)
    outString
  }

  private def formatString(string: String): String = {
    var result = boldRegex.replaceAllIn(string, { m =>
      var boldString = m.matched
      boldString = boldString.substring(2, boldString.length - 2)
      BOLD + boldString + RESET
    })
    result = italicRegex.replaceAllIn(result, { m =>
      var italicString = m.matched
      italicString = italicString.substring(1, italicString.length - 1)
      UNDERLINED + italicString + RESET
    })
    result = inlineCodeRegex.replaceAllIn(result, {m =>
      var codeString = m.matched
      codeString = codeString.substring(1, codeString.length-1)
      BLACK_B + WHITE + BOLD + codeString + RESET
    })
    result = """\\(_|\*|\\|\{|\}|\[|\]|\`|\.|\!|\#|\+|\-|\!)""".r.replaceAllIn(result, _.matched)
    result
  }

  def getLinks(string: String, refs: List[MdLinkRef]) = {
    val labeledLinksMatches = MdLinkRegex.findAllMatchIn(string).toList
    val refLinksMatches = MdLinkWithReferenceRegex.findAllMatchIn(string).toList.filter { m =>
      !labeledLinksMatches.exists(m2 => m.start == m2.start)
    }
    val plainLinkMatches = urlRegex.findAllMatchIn(string).toList.filter { m =>
      !labeledLinksMatches.exists(m2 => m.start > m2.start && m.end < m2.end) && !refLinksMatches.exists(m2 => m.start > m2.start && m.end < m2.end)
    }

    val plainLinks = plainLinkMatches.map(m => (MdLink(m.group(0), None), Ranj(m.start, m.end)))

    val labeledLinks = labeledLinksMatches.map { m =>
      var url = m.group(2)
      url = url.substring(1, url.length - 1).trim
      var label = m.group(1)
      label = label.substring(1, label.length - 1).trim
      (MdLink(url, Some(label)), Ranj(m.start, m.end))
    }

    val refLinks = refLinksMatches.flatMap { m =>
      val (reference, label) = m.group(2) match {
        case null => None
          var ref = m.group(1).trim
          ref = ref.substring(1, ref.length - 1).trim
          (ref, None)
        case other =>
          var ref = other.trim
          ref = ref.substring(1, ref.length - 1).trim
          var lab = m.group(1).trim
          lab = lab.substring(1, lab.length - 1).trim
          (ref, Some(lab))
      }
      refs.find(_.refText==reference) match {
        case None => None
        case Some(mdref) =>
          Some(MdLink(mdref.url, Some(label.getOrElse(reference))),  Ranj(m.start, m.end))
      }
    }

    (plainLinks ++ labeledLinks ++ refLinks).sortWith((l, r) => l._2.start < r._2.start)
  }


}
