package com.gray.note

import com.gray.note.content_things.{ContentString, ContentTag}
import org.scalatest.MustMatchers._

class ContentTagSpec extends BaseSpec {

  "Content Tag" should "initialise properly" in {
    val headerString = "@(this;that)"
    val bodyString = "{this is some body}"
    val tag = new ContentTag(bodyString, headerString)
    assert(tag.contents.length == 1)
    assert(tag.contents(0).isInstanceOf[ContentString])

    tag.labels.get.length mustBe 2
    tag.labels.get(0) mustBe "this"
    tag.labels.get(1) mustBe "that"
  }

  it should "return the trimmed body string" in {
    val headerString = "@(this;that)"
    val bodyString = "{this is some body}"
    val tag = new ContentTag(bodyString, headerString)

    tag.getNeatBody mustBe "this is some body"

    val tag2 = new ContentTag(" [ this is some body] ","@(blah)")
    tag2.getNeatBody mustBe "this is some body"
  }

  it should "default its modifier options properly" in {
    val headerString = "@(this;that)"
    val bodyString = "{this is some body}"
    val tag = new ContentTag(bodyString, headerString)

    tag.isUniversallyReferenced mustBe false
    tag.isParentVisible mustBe false
  }

  it should "specify if it matches a label" in {
    val tag = new ContentTag("{hello there thing \n@(bloo){this is what i want to see}}", "@(blah;blog;blimp)")

    tag.matchesThisLabel("blah") mustBe true
    tag.matchesThisLabel("blog") mustBe true
    tag.matchesThisLabel("blimp") mustBe true
    tag.matchesThisLabel("frump") mustBe false
  }

  it should "specify the full path of a content tag" in {
    val tag = new ContentTag("{@(that){@(the other){content}}}","@(this)")
    val nestedTag = tag.getAllDescendantTags.last

    nestedTag.getFullPath mustBe "this that the other"
    tag.getFullPath mustBe "this"
  }

  it should "specify if it partially matches a label" in {
    val tag = new ContentTag("{hello there thing \n@(bloo){this is what i want to see}}", "@(blah;blog;blimp)")
    val nested = tag.getTagContents(0)
    nested.labels.get(0) mustBe "bloo"
    nested.matchesThisLabelPartially("blah bloo") mustBe Some("blah")
    nested.matchesThisLabelPartially("blog bloo") mustBe Some("blog")
    nested.matchesThisLabelPartially("blimp bloo") mustBe Some("blimp")
  }

  it should "set to paraphrase given square brackets" in {
    val header = "@(^this;that)"
    val body = "[this is some text in square braces]"
    val tag = new ContentTag(body, header)

    tag.labels.get mustBe Array("this", "that")
    tag.isParaphrase mustBe true
  }

  it should "remove modifiers to labels" in {
    val header = "@(^some;thing)"
    val body = "[this is some text in square braces]"
    val tag = new ContentTag(body, header)

    tag.labels.get mustBe Array("some", "thing")
  }

  it should "be parent visible given the option is specified" in {
    val header = " @(^this;that)"
    val body = "[this is some text in square braces]"
    val tag = new ContentTag(body, header)

    tag.isParentVisible mustBe true
  }

  it should "be universally referenced given the option" in {
    val header = "@(*this;that)"
    val body = "[this is some text in square braces]"
    val tag = new ContentTag(body, header)

    tag.isUniversallyReferenced mustBe true

    val controlTag = new ContentTag("{content}", "@(hello;there)")
    controlTag.isUniversallyReferenced mustBe false
  }

  it should "understand the meaning of square braces" in {
    val header = "@(^this;that)"
    val body = "[this is some text in square braces]"
    val tag = new ContentTag(body, header)
    assert(tag.isParaphrase)

    val anotherTag = new ContentTag("{this is not paraphrase}", "@(too;true)")
    anotherTag.isParaphrase mustBe false
  }

  it should "handle tags with no headers" in {
    val tag = new ContentTag("{@(header){tag content}}", "")
    assert(tag.labels.isEmpty)
    tag.contents.length mustBe 1
    tag.contents(0).isInstanceOf[ContentTag] mustBe true
    tag.contents(0).asInstanceOf[ContentTag].labels.get(0) mustBe "header"
    tag.contents(0).asInstanceOf[ContentTag].contents(0).asInstanceOf[ContentString].toString mustBe "tag content"
  }

  it should "yield the tag contents when asked" in {
    val tag = new ContentTag("{content content @(inner1){innter1} content content @(inner2){inner} content content}", "outer")
    tag.getTagContents.length mustBe 2
    tag.getTagContents(0).labels.get(0) mustBe "inner1"
    tag.getTagContents(1).labels.get(0) mustBe "inner2"
  }

  it should "define parent tags to establish tag label inheritance" in {
    val handler = new TestNoteHandler
    for (tag <- handler.baseTags) {
      if (tag.labels.get(0).equals("bliss")) {
        tag.parentTag.get.labels.get(0) mustBe "bardo"
      }
    }

    val tag = new ContentTag("{hello there thing \n@(bloo){this is what i want to see}}", "@(blah)")
    val innerTag = tag.contents(1).asInstanceOf[ContentTag]
    innerTag.parentTag.get mustBe tag
  }

  it should "match plainly by label" in {
    val tag = new ContentTag("{hello there thing \n@(bloo){this is what i want to see}}", "@(blah)")
    tag.matchesThisLabel("blah") mustBe true
  }

  it should "not match given an invalid label" in {
    val tag = new ContentTag("{hello there thing \n@(bloo){this is what i want to see}}", "@(blah)")
    tag.matchesThisLabel("bling") mustBe false
  }

  //TODO implement!
  it should "return an attributed string" in {
    val tag = new ContentTag("{content\n @(inner1){inner content} @(inner2){inner3 content}}","@(label)")
    val atstr = tag.getAttributesString
  }

}
