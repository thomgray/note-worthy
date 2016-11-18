package com.gray.util.attributed_string

import org.scalatest.{FlatSpec, MustMatchers}

class AttributeListSpec extends FlatSpec with MustMatchers {

  "mergeAttribute()" should "" in {

  }
}

object AttributeValidator extends MustMatchers {

  def validateAttributeList(list: List[Attribute]) = {
    for (i <- list.indices){
      val attribute1 = list(i)
      for (j <- i+1 until list.length){
        val attribute2 =list(j)
        attribute1.range.intersect(attribute2.range) mustBe None
      }
    }
  }

  def validateAttributedString(attributedString: AttributedString) =
    validateAttributeList(attributedString.attributes.toList)


}
