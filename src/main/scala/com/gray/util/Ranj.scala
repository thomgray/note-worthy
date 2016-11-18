package com.gray.util

case class Ranj(start: Int, end: Int){
  if (end<start) throw new IllegalArgumentException(s"Range end must be greater than or equal to the start!: Range($start, $end)")
  val length = end-start

  def intersect(range: Ranj) = {
    if (end <= range.start || start >= range.end) None
    else Some(Ranj(Math.max(start, range.start), Math.min(end, range.end)))
  }
  def | (range: Ranj) = intersect(range)

  def union(range: Ranj) = {
    if (end < range.start || start > range.end) None
    else Some(Ranj(Math.min(start, range.start), Math.max(end, range.end)))
  }
  def + (range: Ranj) = union(range)

  def isSubsetOf(ranj: Ranj) = ranj.end >= end && ranj.start <= start

  def exclusion(ranj: Ranj) = intersect(ranj) match {
    case Some(intersect) if intersect.equals(this) => None
    case Some(intersect) =>
      val optionalFirst = start<intersect.start match {
        case true => Some(Ranj(start, intersect.start))
        case _ => None
      }
      val optionalSecond = intersect.end < end match {
        case true => Some(Ranj(intersect.end, end))
        case false => None
      }
      Some(List(optionalFirst, optionalSecond).flatten)
    case None => Some(List(this))
  }

  def contains(int: Int) = start <= int && end > int
  /***
    * Creates a translation of the range by incrementing or decrementing the start and end values by the translation value.
    * <ul>
    *   <li>translate(0) will return a copy of the same range</li>
    *   <li>translate(5) will return Range(10,15) from Range(5,10)</li>
    *   <li>translate(-1) will return Range(4,5) from Range(5,6)
    *</li>
    *
    * @param int
    */
  def translate(int: Int) = Ranj(start+int, end+int)

  override def toString: String = s"($start-$end)"


}
