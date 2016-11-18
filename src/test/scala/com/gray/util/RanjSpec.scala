package com.gray.util

import org.scalatest.{FlatSpec, MustMatchers}

class RanjSpec extends FlatSpec with MustMatchers {

  "intersect()" should "find the range 4,6 between 0,6 and 4,10" in {
    val rng1 = Ranj(0,6)
    val rng2 = Ranj(4,10)
    val intersect = rng1 | rng2
    intersect mustBe Some(Ranj(4,6))
  }

  it should "be symmetrical" in {
    val rng1 = Ranj(0,6)
    val rng2 = Ranj(4,10)
    val intersect = rng1 | rng2
    intersect mustBe rng2|rng1
  }

  it should "not find an intersect between 10,12 and 7,9" in {
    Ranj(10,12) | Ranj(7, 9) mustBe None
  }

  it should "not find an intersect between 1,5 and 5, 8" in {
    Ranj(1,5) | Ranj(5,8) mustBe None
  }

  "union()" should "find the range 2,10 with 2,6 and 5,10" in {
    Ranj(2,10) + Ranj(5,10) mustBe Some(Ranj(2,10))
  }

  it should "find no union between 0,5 and 6,10" in {
    Ranj(0,5) + Ranj(6,10) mustBe None
  }

  it should "find a union between 1,5 and 5,8" in {
    Ranj(1,5) + Ranj(5,8) mustBe Some(Ranj(1,8))
  }

  it should "be symmetrical" in {
    Ranj(4,5) + Ranj(5,6) mustBe Ranj(5,6) + Ranj(4,5)
  }

  "translate()" should "return a copy of the range if translated by 0" in {
    Ranj(5,6) translate(0) mustBe Ranj(5,6)
  }

  it should "tranlate a range by 10" in {
    Ranj(0,1).translate(10) mustBe Ranj(10,11)
  }

  it should "tranlate a range by -4" in {
    Ranj(10,15).translate(-4) mustBe Ranj(6,11)
  }

  "exclusion()" should "return none if the exclusion range is a superset of the range" in {
    var rng = Ranj(10,15)
    var exclusion = Ranj(9,15)
    rng.exclusion(exclusion) mustBe None

    rng = Ranj(1,2)
    exclusion = Ranj(1,2)
    rng.exclusion(exclusion) mustBe None
  }

  it should "return 4,5 if it is 2,5 and 0,4 is excluded" in {


  }

  it should "return 10,15 if it is 10,20 and 15,20 is excluded" in {

  }
}
