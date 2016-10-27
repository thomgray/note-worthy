package com.gray.note

import scala.collection.mutable.ArrayBuffer

trait Formatter {
  def indent(string: String, indent: String) = {
    val linesArray = string.trim.split("\n")
    var outString = ""
    for (line <- linesArray) {
      var editedLine = line.trim
      if (editedLine.startsWith("|")) editedLine = editedLine.substring(1)
      outString = outString + indent + editedLine + "\n"
    }
    outString.substring(0, outString.length - 1) //to remove extra newline
  }

  def indent(string: String, idt: Int): String = {
    var indentString = ""
    for (i <- 0 until idt) indentString = indentString + Config.standardTab
    indent(string, indentString)
  }

  def indentSingleLine(string: String, indent: String, times: Int) = concatenate(indent, times) + string

  def indentSingleLine(string: String, times: Int) = concatenate(Config.standardTab, times) + string

  def mergeStrings[T](collection: TraversableOnce[T], f: (T) => String) = {
    var outString = ""
    for (string <- collection) {
      val transform = f(string)
      outString = outString + "\n" + transform
    }
    outString.substring(1)
  }

  def mergeStringsWithIndexes[T](collection: TraversableOnce[T], f: (T, Int) => String) = {
    var outString = ""
    val indexedSeq = collection.toIndexedSeq
    for (i <- indexedSeq.indices) {
      val string: T = indexedSeq(i)
      val transform = f(string, i)
      outString = outString + "\n" + transform
    }
    outString.substring(1)
  }


  def concatenate(string: String, times: Int) = {
    var out = ""
    for (_ <- 0 until times) out = out + string
    out
  }

  def handleEscapeStrings(string: String) = {
    val pattern = s"(?<!\\\\)${Config.tabEscapeString}"
    string.replaceAll(pattern, Config.standardTab)
  }

  def wrapStringToChars(string:String, length: Int): String = {
    val wrapRegEx = s"(.{0,$length})\\s".r
    wrapRegEx.replaceAllIn(string, m=>m.group(1)+"\n")
  }

  //////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\
  //////////// ATTRIBUTED STRING PRINTING \\\\\\\\\\\\
  //////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\

  /**
    * Takes 10x the time to complete compared to the old method, Only this one works and is much simpler!
    * So think about streamlining it?
    *
    * @param attributedString
    */
  def printFormatted(attributedString: AttributedString): Unit = {

    var currentFormat = Console.RESET

    for (i <- 0 until attributedString.length) {
      val attAtI = attributedString.getAttributesAtIndex(i)
      if (currentFormat equals attAtI) {
        print(attributedString.charAt(i))
      } else {
        print(Console.RESET + attAtI + attributedString.charAt(i))
      }
      currentFormat = attAtI
    }
    print(Console.RESET)
  }

  def printlnFormatted(attributedString: AttributedString) = {
    printFormatted(attributedString)
    print("\n")
  }
}

object Formatter extends Formatter

//////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\
////////////   FORMAT ATTRIBUTE CLASS   \\\\\\\\\\\\
//////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\

/**
  * Case class that defines a format attribute and a range over which that attribute extends in a string
  *
  * @param format ANSI format string or concatenation thereof, defined in the AnsiColor trait
  * @param range  range specifying a start and end index
  */
case class FormatAttribute(format: String, range: parsing.Range) {
  def copy = FormatAttribute(format, range)

  def shift(i: Int) = FormatAttribute(format, parsing.Range(range.start + i, range.end + i))
}


//////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\
////////////  ATTRIBUTED STRING CLASS   \\\\\\\\\\\\
//////////////////////////\\\\\\\\\\\\\\\\\\\\\\\\\\

/**
  * Defines a string and a buffer of attribute objects mapping attributes to ranges.
  *
  * @param str optional initial string
  */
class AttributedString(str: String = "") {
  var string = str
  val attributes = new ArrayBuffer[FormatAttribute]()

  def +=(extension: String, formatAttributes: Option[FormatAttribute] = None) = {
    string = string + extension
    if (formatAttributes.isDefined) {
      attributes += formatAttributes.get
    }
  }

  def +=(attributedString: AttributedString) = {
    val offset = string.length
    string = string + attributedString.string
    for (attribute <- attributedString.attributes) {
      val at2 = attribute.shift(offset)
      attributes += at2
    }

  }

  def +(attributedString: AttributedString) = {
    val out = this.copy
    out += attributedString
    out
  }

  def length = string.length

  def charAt(i: Int) = string.charAt(i)

  def +=(extension: String, format: String) = {
    val range = parsing.Range(string.length, string.length + extension.length)
    string = string + extension
    addAttribute(format, range)
  }

  def addAttribute(format: String, range: parsing.Range) = {
    attributes += FormatAttribute(format, range)
  }

  /**
    * @deprecated Don't need this method for printing!
    * @return
    */
  def getAttributedStringFormatBuffer = {
    val stringFormatBuffer = new ArrayBuffer[(String, String)]()
    var index = 0

    for (attribute <- attributes) {
      val range = attribute.range
      if (range.start > index) {
        val substring = string.substring(index, range.start)
        stringFormatBuffer += ((substring, ""))
        index = range.start
      }
      val substring = string.substring(range.start, range.end)
      stringFormatBuffer += ((substring, attribute.format))
      index = range.end
    }
    if (index < string.length) {
      val substring = string.substring(index, string.length)
      stringFormatBuffer += ((substring, ""))
    }
    stringFormatBuffer.toArray
  }

  def getAttributesAtIndex(i: Int) = {
    var atts = ""
    for (attribute <- attributes if attribute.range.contains(i)) {
      atts = atts + attribute.format
    }
    atts match {
      case "" => Console.RESET
      case str => str
    }
  }

  def copy = {
    val out = new AttributedString()
    out.string = this.string
    for (at <- attributes) out.attributes += at.copy
    out
  }

}




