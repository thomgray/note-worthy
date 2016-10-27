package com.gray.parse

import com.gray.parse.mdlparse.MdlIterator
import scala.collection.mutable.ArrayBuffer

trait Parser {
  private[gray] def getIterator: ParseIterator

  def parseForResults = {
    val iterator = getIterator
    val buffer = new ArrayBuffer[ParseResult]()
    while (iterator.nextThing match {
      case Some(result) => buffer += result
        true
      case None => false
    }){}
    buffer.toList
  }

}


class MdlParser(string: String) extends Parser{
  override def getIterator: ParseIterator = new MdlIterator(string)
}

object MdlParser {
  def apply(string: String): MdlParser = new MdlParser(string)
}