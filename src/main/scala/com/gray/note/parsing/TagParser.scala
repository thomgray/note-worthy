package com.gray.note.parsing

import com.gray.note.content_things.{Content, ContentString, ContentTag}

import scala.collection.mutable.ArrayBuffer

object TagParser {

  def getContent(rawString: String): Array[Content] = {
    val buffer = new ArrayBuffer[Content]()
    var index = StringIterator.nextSolidCharIndex(0, rawString)
    while (
      StringIterator.rangeOfNextTagHeaderAndBody(index, rawString) match {
        case None =>
          StringIterator.nextSolidCharIndex(index, rawString) match {
            case -1 => false
            case x => buffer += new ContentString(rawString.substring(x).trim)
              false
          }
        case Some((headRng, bodRng)) =>
          if (index < headRng.start) {
            buffer += new ContentString(rawString.substring(index, headRng.start))
            index = headRng.start
            true
          } else {
            buffer += new ContentTag(rawString.substring(bodRng.start, bodRng.end),
              rawString.substring(headRng.start, headRng.end))
            index = StringIterator.nextSolidCharIndex(bodRng.end, rawString)
            true
          }
      }
    ) {}
    buffer.toArray
  }

}
