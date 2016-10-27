package com.gray.note.parsing

/**
  *Dedicated object for parsing ranges from strings
  */
object StringIterator {
  /**
    * Returns the index of the next solid character beginning at the parameter index of the specified string. Hence if the string(begin) is a solid character, returns begin, else returns the next solid character. If the end of the string is reached first, returns -1
    *
    * @param begin the starting index of the search (inclisive)
    * @param string the string to be parsed
    * @return index of next solid character inclusive of the begin parameter index, or -1 if the end of the string is reached
    */
  def nextSolidCharIndex(begin: Int = 0, string: String): Int = {
    if (begin < 0) return -1
    for (i <- begin until string.length) if (!Character.isWhitespace(string(i))) return i
    -1
  }

  /**
    * Returns the index of the next sequence in the form ..'@('.. provided the sequence doesn't begin with a '\' backslash, starting from the specified index
    *
    * @param index     the starting position
    * @param rawString the string to be parsed
    * @return Option of the index of the next '@' character given the specified regex, None if no match is found
    */
  def getIndexOfNextTagHeader(index: Int, rawString: String): Option[Int] = {
    var i: Int = index
    while (
      nextSolidCharIndex(i, rawString) match {
        case -1 => i = -1; false
        case (x) => i = x; true
      }
    ) {
      if (rawString(i) == '@' &&
            i + 2 < rawString.length &&
            rawString(i + 1) == '(' &&
            (i == 0 || rawString(i - 1) != '\\')
      ) return Some(i)
      i += 1
    }
    None
  }

  /**
    * Returns an optional range of the next braced expression provided the next solid character from the parameter index character (inclusive) is an opening brace.
    *
    * @param curlyBraces specify true for curly brace, false for square braces
    * @param index       the parting position
    * @param string      the string to be parsed
    * @return Optional range of the next braced sequence in the parameter string, None if the next solid character is <i>NOT</i> an open brace (curly or square as specified)
    */
  def getRangeOfBracedExpression(curlyBraces: Boolean, index: Int, string: String): Option[Range] = {
    val begin = nextSolidCharIndex(index, string)
    if (begin < 0) return None
    val openingChar = string(begin)
    (openingChar, curlyBraces) match {
      case ('{', true) | ('[', false) =>
        var currentIndex = begin + 1
        var lr = 1
        while (
          nextSolidCharIndex(currentIndex, string) match {
            case -1 => false
            case (x) =>
              currentIndex = x
              val currentChar = string(x)
              (currentChar, curlyBraces) match {
                case ('{', true) | ('[', false) => lr += 1
                case ('}', true) | (']', false) => lr -= 1
                case _ => Unit
              }
              if (lr == 0) return Some(Range(begin, x + 1))
              currentIndex = x + 1
              true
          }
        ) {}
        None
      case _ => None
    }
  }

  /**
    * Similar to getRangeOfBracedExpression(Bool, Int, String), only returns the range of braced sequence whether curly or square. Confirm whether the returned range is curly of square by checking <ul><li> string(range.start)=='{'</li></ul>
    *
    * @param index
    * @param string
    * @return Option of range of next braced expression. See
    */
  def getRangeOfBracedExpression(index: Int, string: String): Option[Range] = {
    val begin = nextSolidCharIndex(index, string)
    if (begin < 0) return None
    val openingChar = string(begin)

    openingChar match {
      case '{' | '[' =>
        val curlyBraces = openingChar == '{'
        var currentIndex = begin + 1
        var lr = 1
        while (
          nextSolidCharIndex(currentIndex, string) match {
            case -1 => false
            case (x) =>
              val currentChar = string(x)
              (currentChar, curlyBraces) match {
                case ('{', true) | ('[', false) => lr += 1
                case ('}', true) | (']', false) => lr -= 1
                case _ => Unit
              }
              if (lr == 0) return Some(Range(begin, x + 1))
              currentIndex = x + 1
              true
          }
        ) {}
        None
      case _ => None
    }
  }

  /**
    * Returns an optional range of the next tag header given the tag header begins at the specified index.
    *
    * @param index  the index of the initial '@' character of the tag
    * @param string the source string
    * @return Some(Range) of the tag header including '@(...)', or None if the next solid characters are not in the form '@(' and the subsequent string is not a valid tag header
    */
  def getRangeOfTagHeader(index: Int, string: String): Option[Range] = {
    val beginIndex = nextSolidCharIndex(index, string)
    if (beginIndex < 0 || string(beginIndex) != '@') return None
    val braceIndex = nextSolidCharIndex(beginIndex + 1, string)
    if (braceIndex < 0 || string(braceIndex) != '(') return None

    var currentIndex = braceIndex + 1
    var lr = 1
    while (
      nextSolidCharIndex(currentIndex, string) match {
        case -1 => false
        case (x) =>
          string(x) match {
            case '(' => lr += 1
            case ')' => lr -= 1
            case _ =>
          }
          if (lr == 0) return Some(Range(beginIndex, x + 1))
          currentIndex = x + 1
          true
      }

    ) {}
    None
  }

  /**
    * Returns the range of the next tag header or none if none occurs from the specified index
    *
    * @param index
    * @param string
    * @return
    */
  def rangeOfNextTagHeader(index: Int, string: String): Option[Range] =
  getIndexOfNextTagHeader(index, string) match {
    case Some(x) => getRangeOfTagHeader(x, string)
    case _ => None
  }

  /**
    * Returns an optional tuple of the next tag header range with the corresponding tag body range, or None if it fails
    *
    * @param index
    * @param string
    * @return tuple of the next tag header range with the corresponding body
    */
  def rangeOfNextTagHeaderAndBody(index: Int, string: String): Option[(Range, Range)] =
  rangeOfNextTagHeader(index, string) match {
    case Some(rng) =>
      getRangeOfBracedExpression(rng.end, string) match {
        case Some(bodRng) => Some((rng, bodRng))
        case _ => None
      }
    case _ => None
  }


}

case class Range(start: Int, end: Int) {
  override def equals(obj: scala.Any): Boolean = obj match {
    case Range(b, e) => (b == start) && (e == end)
    case _ => false
  }

  def contains(i: Int) = start <= i && i < end
}
