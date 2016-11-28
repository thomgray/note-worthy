package com.gray.markdown.formatting

import java.util.regex.Matcher

import com.gray.util.Ranj
import com.gray.util.attributed_string.AttributedString

import scala.collection.mutable
import scala.io.AnsiColor
import scala.util.matching.Regex

trait CodeColouring extends AnsiColor {

  val colourSchemeJava: Map[String, String] = Map.empty
  val colourSchemeScala: Map[String, String] = Map.empty

  val CSKeyBackGroundColour = "backgroundColour"
  val CSKeyPlainCodeColour = "plainCodeColour"
  val CSKeyCommentColour = "commentColour"
  val CSKeyStringColour = "commentColour"

  def gerRangesForRegex(string: String, regex: Regex) = regex.findAllMatchIn(string).map(m => Ranj(m.start, m.end)).toList

  def getRangesForRegexes(string: String, regex: Regex*) = regex.flatMap(_.findAllMatchIn(string).map(m => Ranj(m.start, m.end))).toList

  private def segmentScala(str: AttributedString) = {
    str.segment(commentRegex).flatMap { s =>
      if (!s.string.matches(commentRegex.toString())) {
        s.segment(tripleQuoteRegex).flatMap { ss =>
          if (!matches(ss.string, "^s?\"\"\"".r)){
            ss.segment(scalaSingleQuoteRegex)
          }else List(ss)
        }
      } else List(s)
    }
  }

  private def segment(str: AttributedString, regexes: Regex *): List[AttributedString] = {
    if (regexes.isEmpty) List(str)
    else str.segment(regexes.head) flatMap { s =>
      if (regexes.length>1 && !matches(s.string, regexes.head)) {
        segment(s, regexes.drop(1):_*)
      }else List(s)
    }
  }

  def colourScala(str: AttributedString) = {
    val segmented = segmentScala(str)
    val transform = segmented map { string =>
      var _str = string
      if (matches(_str.string, "^(\\/\\/|\\/\\*)".r)){
        _str = _str.addAttribute(Seq(GREEN))
      }
      else if (matches(_str.string, "^s?\"".r)){
        _str = _str.addAttribute(Seq(CYAN))
        if (!matches(_str.string, "^s?\"\"\"".r)){
          _str = _str.addAttributeForRegex("\\\\(u[0-9a-f]{4}|.)".r, Seq(RED))
        }
        if (_str.string.startsWith("s")){
          _str = _str.addAttributeForRegexGroups("(?<!\\$)(\\$)(?!\\$)(\\w+)".r, Map(1 -> Seq(WHITE), 2 -> Seq(MAGENTA)))
          _str = _str.addAttributeForRegex("\\$\\$".r, Seq(RED))
        }
      }else{
        _str = _str.addAttributeForRegex(keyWordsRegex(protectedWordsScala), Seq(RED))
        _str = _str.addAttributeForRegex(keyWordsRegex(scalaBasicTypes), Seq(BLUE))
        _str = _str.addAttributeForRegexGroups(scalaFunctionRegex, Map(2 -> Seq(YELLOW)))
        _str = _str.addAttributeForRegex("\\b\\d+\\b".r, Seq(MAGENTA))
        _str = _str.addAttributeForRegexGroups("\\b(var +|val +)(\\w+)".r, Map(2 -> Seq(MAGENTA)))

      }
      _str
    }
    AttributedString.mkString(transform)
  }

  /// general
  private val tripleQuoteRegex = "s?\"{3,}.*?\"{3,}".r
  private val scalaSingleQuoteRegex = "s?\".*?((?<!\\\\)\")".r
  private val scalaQuote = "s?\"{3,}(.|\\n)*?\"{3,}|s?\".*?((?<!\\\\)\")".r

  private val singleQuoteRegex = "\".*?((?<!\\\\)\")".r

  private val commentRegex = """(\/{2,}.*(\n|$)|\/\*(.|\n)*?\*\/)""".r

  private def matches(string: String, regex: Regex) = {
    regex.findFirstIn(string).isDefined
  }

  //  private def keyWordsRegex(string: String) = s"(^|\\s)(($string)(\\s+|$$))+".r
  private def keyWordsRegex(string: String) = s"\\b($string)\\b".r
  private def keySymbolRegex(string: String) = s"(\\b|\\s)($string)(\\b|\\s)".r

  private def segmentStringWithRegex(string: String, regex: Regex) = {
    val outBuffer = new mutable.MutableList[String]()
    var lastIndex = 0
    regex.findAllMatchIn(string).toList.foreach { mtch =>
      if (mtch.start > lastIndex) outBuffer += string.substring(lastIndex, mtch.start)
      outBuffer += string.substring(mtch.start, mtch.end)
      lastIndex = mtch.end
    }
    if (lastIndex < string.length) outBuffer += string.substring(lastIndex, string.length)
    outBuffer.toList
  }


  //scala
  private val protectedWordsScala = "def|val|var|this|if|else|case|for|yield|while|do|match|try|throw|catch|return|class|trait|object|public|private|protected|abstract|implicit|import|package|extends|with"
  private val operationWordsScala = "\\+|-|->|=>|<-|&&|\\|\\|"
  private val scalaBasicTypes = "String|Boolean|Int|Double|Float|List|Array|Map|Seq"
  private val scalaFunctionRegex = "\\b(def +)(\\w+)\\b".r


  private[formatting] def splitCodeAndStringsScala(string: String) = {
    def segmentTripleQuote(string: String) = segmentStringWithRegex(string, tripleQuoteRegex)
    def segmentDoubleQuote(string: String) = if (matches(string, "^s?\"\"\"".r)) List(string) else segmentStringWithRegex(string, scalaSingleQuoteRegex)
    def segmentComments(string: String) = if (matches(string, "^s?\"".r)) List(string) else segmentStringWithRegex(string, commentRegex)

    for {
      str1 <- segmentTripleQuote(string)
      str2 <- segmentDoubleQuote(str1)
      str3 <- segmentComments(str2)
    } yield str3
  }

  // java
  private val protectedWordsJava = "public|private|protected|class|interface|void|static|final|abstract|return|if|else|case|do|while"

  def colorJavaCode(string: String, fgColor: String) = {
    string
  }
}
