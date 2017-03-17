package com.gray.note.handling

import com.gray.markdown.{@@, MdHeader, MdLocation, MdString}
import com.gray.note.content_things.ContentLoader
import com.gray.note.util.ResourceIO
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import com.gray.note.content_things._
import com.gray.parse.mdparse.MdIterator
import com.gray.parse.{Location, ParseConstants, ParseResult}

import scala.language.implicitConversions


class SearchEngineSpec extends FlatSpec with Matchers with MockitoSugar with ParseConstants with BeforeAndAfter {

  implicit def thingToList[T](thing: T): List[T] = List(thing)
  implicit def thingToOpt[T](thing: T): Option[T] = Some(thing)
  implicit def thingToOptList[T](thing: T): Option[List[T]] = Some(List(thing))

  val mockIO = mock[ResourceIO]
  val mockLoader = mock[ContentLoader]

  val location = MdLocation(0,0)

  val engine = new SearchEngine {
    override private[handling] val resourceIO: ResourceIO = mockIO
    override private[handling] val contentLoader: ContentLoader = mockLoader
  }

  before {
    reset(mockLoader)
    reset(mockIO)
  }

  "getContentWitQuery" should "find content tags matching a search string" in {
    val alias = new ContentTagAlias(
      "taglabel",
      List("alias"),
      @@(0,0)
    )

    val someTag = new ContentTag(
      List(new ContentString(List(MdString("", @@(0,0))), "", @@(0,0))),
      MdHeader(MdString("tagLabel", location), 1, location),
      Nil,
      location
    )

    when (mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory(anyString())) thenReturn List(alias, someTag)

    engine.getContentWithQuery("taglabel") shouldBe List(someTag)
  }

//
//
//  it should "find aliased content" in {
//    val alias = new ContentTagAlias(ParseResult("taglabel", "alias", CONTENT_ALIAS, ""))
//    val someTag = new ContentTag(ParseResult("some tag", "taglabel", CONTENT_TAG, ""))
//
//    when (mockIO.getDirectories) thenReturn List("foo")
//    when(mockLoader.getContentFromDirectory(anyString())) thenReturn List(alias, someTag)
//
//    engine.getContentWithQuery("alias") shouldBe List(someTag)
//  }
//
//  it should "find aliased content nested within a tag" in {
//    val alias = ContentTagAlias("aliased content", "alias")
//    val aliasedTag = ContentTag("hippo", "aliased content", Location(0,0))
//    val tagContainingAlias = ContentTag("anything", "upper", Location(0,0), List(alias, aliasedTag))
//
//
//    when (mockIO.getDirectories) thenReturn List("foo")
//    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(tagContainingAlias)
//
//    val query = "upper alias"
//    val result = engine.getContentWithQuery(query)
//
//    verify(mockLoader, times(1)).getContentFromDirectory("foo")
//
//    result shouldBe List(aliasedTag)
//  }
//
//  "tagMatcherSearchString" should "match an alias with a search string" in {
//    val alias = ContentTagAlias("alias", "label")
//
//    engine.tagMatchesSearchString(alias, "label") shouldBe true
//  }
//
//  it should "match an alias with a tag parent" in {
//    val alias = ContentTagAlias("alias", "label")
//    val contentTag = ContentTag("goo", "content label", location, alias)
//
//    engine.tagMatchesSearchString(alias, "content label label") shouldBe true
//  }
//
//  it should "match a nested content tag" in {
//    val tag1 = ContentTag("foo", "tag1",location)
//    val tag2 = ContentTag("foo", "tag2", location, List(tag1))
//
//    engine.tagMatchesSearchString(tag1, "tag2 tag1") shouldBe true
//  }
//
//  "getAllContentTagLikeThings" should "get content tags and aliases" in {
//    val alias = ContentTagAlias("aliased", "alias")
//    val aliasedTag = ContentTag("hippo", "hippo", Location(0,0))
//    val anotherTag = ContentTag("giraffe", "giraffe", Location(0,0))
//
//    when (mockIO.getDirectories) thenReturn List("foo")
//    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(alias, anotherTag, aliasedTag)
//
//    engine.getAllContentTagLikeThings shouldBe List(alias, anotherTag, aliasedTag)
//  }
//
//  it should "get nested content including aliases" in {
//    val alias = ContentTagAlias("aliased", "alias")
//    val aliasedTag = ContentTag("hippo", "hippo", Location(0,0))
//    val anotherTag = ContentTag("giraffe", "giraffe", Location(0,0), List(alias, aliasedTag))
//
//    when (mockIO.getDirectories) thenReturn List("foo")
//    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(anotherTag)
//
//    engine.getAllContentTagLikeThings.toSet shouldBe List(alias, anotherTag, aliasedTag).toSet
//  }
}
