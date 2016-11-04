package com.gray.markdown.parsing

import com.gray.markdown._

import scala.collection.mutable
import scala.util.matching.Regex


object MdParser {
  def parse(string: String) = {
    val parser = new MdParsingBot(string)
    parser.parse()
    parser.paragraphs.toList.filter {
      case MdBreak() => false
      case _ => true
    }
  }
}
