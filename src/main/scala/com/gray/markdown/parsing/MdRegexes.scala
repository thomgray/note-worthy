package com.gray.markdown.parsing

import scala.util.matching.Regex

protected[parsing] trait MdRegexes extends Regexes{

  private val nl = "(\\n|\\r)"
  val leadingWhitespaceRegex = "^\\s*".r

  val horizontalLineRegex = " *-{3,} *".r

  val codeBeginRegex = "^ {0,3}(`{3,}|~{3,}) *(\\w+)? *$".r
  val codeEndRegex = "^ {0,3}(`{3,}|~{3,}) *$".r
  val indentedLiteralRegex = "^ {4,}.*".r

  // list regexes
  val bulletListItemRegex = "^ {0,3}(-|\\*|\\+) {1,4}(?!\\[(x| )\\] ).*$".r
  val numberedListItemRegex = "^ {0,3}\\d+\\. {1,4}.*".r
  val checkListItemRegex = "^ {0,3}(-|\\+|\\*) +\\[(x| )\\] +.*$".r

  val listItemPrefixRegex = "^ *(((-|\\+|\\*)( {1,4}(\\[( |x)\\]))?)|(\\d+\\.)) {1,}".r
  val bulletOrNumberPrefix = "^ *((\\d+.)|(-|\\+|\\*)) {1,}".r

  val MdLinkRegex = "(\\[[^\\]]+?\\])( {0,1}\\([^\\)]+?\\))".r
  val MdLinkWithReferenceRegex = "(\\[[^\\]]+?\\])( {0,1}\\[[^\\]\\)]+?\\])?".r
  val MdLinkRefRegex = "^ {0,3}\\[([^\\]]+?)\\] *: *(\\S+) *$".r

  ///
  val blockQuoteRegex = "^ {0,3}> {0,4}\\S.*".r

  /**
    * Anything matching italic will match within bold but not visa versa, so be sure to allow for this by handling bold replacements first!
    */
  val italicRegex = """(?:(?<!\\)\*(.*?)(?<!\\)\*)|(?:(?<!\\)_(.*?)(?<!\\)_)""".r
  /**
    * Must replace bold first as it italic regex will match within the bold!!
    */
  val boldRegex = """(?:(?<!\\)\*\*(.*?)(?<!\\)\*\*)|(?:(?<!\\)__(.*?)(?<!\\)__)""".r

  val inlineCodeRegex = """(?<!\\)`.+?(?<!\\)`""".r


  ///table-related regexes
  val tableHeaderRegex = """table header""".r
  /*
  its dash-bar with optional bar at the beginning and optional dash at the end
  Or just bar dash
  */
  val tableSeparatorRegex =
  """^\s*\|?\s*((:?-+:?)\s*\|\s*)+(:?-+:?)?\s*$|\s*\|\s*(:?-+:?)\s*""".r

  //unambiguous single lines
  val headerRegex = "^ {0,3}#{1,5} +.*$".r
  val emptyLineRegex = "^\\s*$".r


  def anyMatchingRegex(string: String, regexes: Seq[Regex]) = regexes.find(_.findFirstIn(string).isDefined)

  def allMatchingRegex(string: String, regexes: Seq[Regex]) = regexes.filter(_.findFirstIn(string).isDefined)

  def matchString(regex: Regex, string: String) = regex.findFirstIn(string).isDefined
}

trait Regexes {
  val urlRegex = "\\b(https?:\\/\\/)?(www\\.)?[A-Za-z0-9\\-._~:\\/?#\\[\\]@!$&'()*+,;=`.%]+\\.([a-z]{2}|com|net|org|edu|int|gov|mil)(\\/[A-Za-z0-9\\-._~:\\/?#\\[\\]@!$&'()*+,;=`.%]*)?\\b".r
}