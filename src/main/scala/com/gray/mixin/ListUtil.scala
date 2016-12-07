package com.gray.mixin

trait ListUtil {

  def trimBlankLinesFromList(list: List[String]) =
    trimFromList(list)("^\\s*$".r.findFirstIn(_).isDefined)

  def trimFromList[T](list: List[T])(predicate: T => Boolean) =
    list.reverse.dropWhile(predicate).reverse.dropWhile(predicate)


}
