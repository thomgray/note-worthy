package com.gray.util.attributed_string

import com.gray.util.Ranj

import scala.collection.generic._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Attribute(range: Ranj, formats: Formats) {
  val formatString = formats.formatString

  def copyInRange(range1: Ranj) = range.intersect(range1) match {
    case Some(intersect) =>
      val newRng = Ranj(intersect.start - range1.start, intersect.end - range1.start)
      val newAtt = Attribute(newRng, formats)
      Some(newAtt)
    case None => None
  }

  def translate(int: Int) = Attribute(range.translate(int), formats)

  def isSameAs(attribute: Attribute) = formats.equals(attribute.formats)

  /**
    * Merges attributes if they are similar and overlapping, if they intersect but are dissimilar, returns a sequence of attributes by breaking the overlap of this attribute and the parameter attribute. In that case the result sequence may have:
    * <ul>
    * <li>3 attributes (if neither is a subset of the other)</li>
    * <li>2 attributes (if one is a subset of the other)</li>
    * <li>1 attribute (if both are subsets of eachother - i.e. thier ranges are equivalent). In the case of format conflicts, the parameter formats overwrite the current formats</li>
    * </ul>
    *
    * @param attribute the attribute to be merged with this one
    * @return
    */
  def merge(attribute: Attribute) = {
    (isSameAs(attribute), range.union(attribute.range), range.intersect(attribute.range)) match {
      case (true, Some(union), _) =>
        Some(List(Attribute(union, formats)))
      case (false, Some(union), Some(intersection)) =>
        val intersectionAttribute = List(Attribute(intersection, Formats.merge(formats, attribute.formats).toList))
        val thisAttribute = range.exclusion(intersection.union(attribute.range).get) match {
          case Some(newRanges) => newRanges.map(Attribute(_, formats.toList))
          case _ => List.empty
        }
        val otherAttribute = attribute.range.exclusion(range.union(intersection).get) match {
          case Some(newRanges) => newRanges.map(Attribute(_, attribute.formats.toList))
          case None => List.empty
        }
        Some(List(thisAttribute, intersectionAttribute, otherAttribute).flatten.sortWith(Attribute.sortFunction))
      case _ =>
        None
    }
  }

}

object Attribute {
  def apply(range: Ranj, formatsSequence: Seq[String]): Attribute = new Attribute(range, Formats(formatsSequence))

  protected[util] def sortFunction(att1: Attribute, att2: Attribute) = att1.range.start <= att2.range.start
}

class AttributeList(attribute: Seq[Attribute]) extends mutable.Traversable[Attribute] with Serializable {

  import Attribute.sortFunction

  private var _list: List[Attribute] = attribute.toList.sortWith(sortFunction)

  def +(attribute: Attribute) = _list = mergeAttribute(attribute)// (_list :+ attribute).sortWith(sortFunction)

  def length = _list.length

  def sort = _list sortWith sortFunction

  def getAttributesInRange(ranj: Ranj) = _list.flatMap(_.copyInRange(ranj))

  override def head: Attribute = _list.head

  def mergeAttribute(attribute: Attribute) = mergeOverlaps(_list :+ attribute, attribute)


  private def mergeOverlaps(list: List[Attribute], priorityAtt: Attribute) = list.sortWith(sortFunction).foldLeft(List.empty[Attribute]) { (outboundList, att) =>
    outboundList.lastOption match {
      case None => outboundList :+ att
      case Some(attribute) if att == priorityAtt =>
        outboundList.dropRight(1) ++ attribute.merge(att).getOrElse(List(attribute, att))
      case Some(attribute) => outboundList.dropRight(1) ++ att.merge(attribute).getOrElse(List(attribute, att))
    }
  }


  private def mergeIsRedundant(attribute: Attribute) =
    _list.exists(at => at.isSameAs(attribute) && attribute.range.isSubsetOf(at.range))

  private def mergeExtendsPreexisting(attribute: Attribute) =
    _list.find(at => at.isSameAs(attribute) && at.range.union(attribute.range).isDefined).map(at => Attribute(at.range.union(attribute.range).get, at.formats.toList))

  override def foreach[U](f: (Attribute) => U): Unit = for (at <- _list) f(at)

}

