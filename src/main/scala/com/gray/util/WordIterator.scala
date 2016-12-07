package com.gray.util

class WordIterator(string: String) {

  private val words = string.split("\\s+").toList
  private var i = 0

  def next = if (hasNext) {
    i += 1
    Some(words(i-1))
  }else None

  def hasNext = i < words.length

  def iterate(f: (String) => Unit ) =  while (hasNext) f(words(i))

  def toList = words
}

object WordIterator {
  def apply(string: String): WordIterator = new WordIterator(string)
}