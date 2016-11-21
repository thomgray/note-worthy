package com.gray.markdown.parsing

import com.gray.markdown._


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
