package com.gray.markdown.parsing

import com.gray.markdown.{MdLink, MdLinkRef}
import org.scalatest.{FlatSpec, MustMatchers}

class DefaultMdFactorySpec extends FlatSpec with MustMatchers{

  val factory = new DefaultMdFactory()

  "getLinks()" should "find plain URLs" in {
    val string = """this is some string with a url www.google.co.uk and another https://www.wilfried.jz"""
    val links = factory.getLinks(string, List.empty)
    links.length mustBe 2
    links(0)._1 mustBe MdLink("www.google.co.uk", None)
    links(1)._1 mustBe MdLink("https://www.wilfried.jz", None)
  }

  it should "find labeled urls" in {
    val string = """this is a string with a [google](www.google.com) link that is labeled"""
    val links = factory.getLinks(string, List.empty)
    links.length mustBe 1
    links(0)._1 mustBe MdLink("www.google.com", Some("google"))
  }

  it should "find referenced links" in {
    val string =  """this is a string with a [google] reference as well as a [go to google][google] reference"""
    val links = factory.getLinks(string, List(MdLinkRef("www.google.com", "google")))
    links.length mustBe 2

    links(0)._1.url mustBe "www.google.com"
    links(0)._1.inlineString mustBe Some("google")
    links(1)._1.url mustBe "www.google.com"
    links(1)._1.inlineString mustBe Some("go to google")
  }



}
