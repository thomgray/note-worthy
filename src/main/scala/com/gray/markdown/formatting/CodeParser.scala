package com.gray.markdown.formatting

import java.util.regex.Matcher

import com.gray.util.Ranj
import com.gray.util.attributed_string.AttributedString

import scala.collection.mutable
import scala.io.AnsiColor
import scala.util.matching.Regex

trait CodeParser extends AnsiColor {

  def gerRangesForRegex(string: String, regex: Regex) = regex.findAllMatchIn(string).map(m => Ranj(m.start, m.end)).toList

  def getRangesForRegexes(string: String, regex: Regex*) = regex.flatMap(_.findAllMatchIn(string).map(m => Ranj(m.start, m.end))).toList

  private def segmentScala(str: AttributedString) = {
    str.segment(commentRegex).flatMap { s =>
      if (!s.string.matches(commentRegex.toString())) {
        s.segment(tripleQuoteRegex).flatMap { ss =>
          if (!ss.string.startsWith("\"\"\"")){
            ss.segment(scalaSingleQuoteRegex)
          }else List(ss)
        }
      } else List(s)
    }
  }

  def colourScala(str: AttributedString) = {
    val segmented = segmentScala(str)
    val transform = segmented map { string =>
      var _str = string
      if (matches(_str.string, "^(\\/\\/|\\/\\*)".r)){
        _str = _str.addAttribute(Seq(YELLOW))
        _str = _str.addAttributeForRegex("\\\\".r, Seq(MAGENTA))
      }
      else if (matches(_str.string, "^s?\"\"\"".r)){
        _str = _str.addAttribute(Seq(CYAN))
      }
      else if (matches(_str.string, "^s?\"".r)){
        _str = _str.addAttribute(Seq(CYAN))
      }else{
        _str = _str.addAttributeForRegex(keyWordsRegex(protectedWordsScala), Seq(RED))
        _str = _str.addAttributeForRegex(keyWordsRegex(operationWordsScala), Seq(BLUE))
        _str = _str.addAttributeForRegex(keyWordsRegex(scalaBasicTypes), Seq(BLUE))
        _str = _str.addAttributeForRegexGroups(scalaFunctionRegex, Map(2 -> Seq(GREEN)))

      }
      _str
    }
    AttributedString.mkString(transform)
  }

  /// general
  private val tripleQuoteRegex = "s?\"{3,}.*?\"{3,}".r
  private val scalaSingleQuoteRegex = """s?"([^"]|\\")*[^\\]"""".r
  private val singleQuoteRegex = """"([^"]|\\")*[^\\]"""".r

  private val commentRegex = """(\/{2,}.*(\n|$)|\/\*(.|\n)*?\*\/)""".r

  private def matches(string: String, regex: Regex) = {
    regex.findFirstIn(string).isDefined
  }

  //  private def keyWordsRegex(string: String) = s"(^|\\s)(($string)(\\s+|$$))+".r
  private def keyWordsRegex(string: String) = s"\\b($string)\\b".r

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
  private val protectedWordsScala = "def|val|var|this|if|else|case|for|while|do|match|try|throw|catch|return|class|trait|object|public|private|protected|abstract|implicit|import|package|extends|with"
  private val operationWordsScala = "\\+|-|->|=>|<-"
  private val scalaBasicTypes = "String|Boolean|Int|Double|Float|List|Array|Map|Seq"
  private val scalaFunctionRegex = "\\b(def +)(\\w+)\\b".r
  private val scalaVarValRegex = "\\b(val +|var +)(\\w+)\\b".r

  def colorScalaCode(string: String, fgColor: String) = {
    val segmented = splitCodeAndStringsScala(string)
    (segmented map { str =>
      if (matches(str, "^s?\"".r)) {
        var _str = "\\$(\\w+|\\{.+\\})".r.replaceAllIn(str, m => fgColor + Matcher.quoteReplacement(m.toString) + CYAN)
        _str = "\\\\.".r.replaceAllIn(_str, m => MAGENTA + Matcher.quoteReplacement(m.toString()) + CYAN)
        CYAN + _str + fgColor
      }
      else if (matches(str, "^(\\/\\/|\\/\\*)".r)) YELLOW + str + fgColor
      else {
        var _str = str
        _str = scalaFunctionRegex.replaceAllIn(_str, m => m.group(1) + GREEN + m.group(2) + fgColor)
        _str = scalaVarValRegex.replaceAllIn(_str, m => m.group(1) + MAGENTA + m.group(2) + fgColor)
        _str = keyWordsRegex(protectedWordsScala).replaceAllIn(_str, m => RED + m.toString() + fgColor)
        _str = s"($operationWordsScala)".r.replaceAllIn(_str, m => YELLOW + m.group(1) + fgColor)
        _str = keyWordsRegex(scalaBasicTypes).replaceAllIn(_str, m => BLUE + m.toString() + fgColor)
        _str
      }
    }).mkString
  }

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
