package com.gray.util

import org.scalatest.{FlatSpec, MustMatchers}
import org.scalatest.exceptions.TestFailedException

class WordIteratorSpec extends FlatSpec with MustMatchers {

  "iterate" should "iterate every word in a string" in {
    val iterator = WordIterator("this is a string")
    var i = 0
    iterator.iterate{s =>
      s mustBe (i match {
        case 0 => "this"
        case 1 => "is"
        case 2 => "a"
        case 3 => "string"
        case 4 => throw new TestFailedException("Too much iteration going on", 10)
      })
      i += 1
    }
  }


}
