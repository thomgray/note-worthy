package com.gray.note.handling

import com.gray.markdown.{@@, MdHeader, MdLocation, MdString}
import com.gray.note.content_things.{ContentLoader, _}
import com.gray.note.util.ResourceIO
import com.gray.parse.{Location, ParseConstants}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.language.implicitConversions

class SearchEngineSpec extends FlatSpec with Matchers with MockitoSugar with ParseConstants with BeforeAndAfter {

  implicit def thingToList[T](thing: T): List[T] = List(thing)
  implicit def thingToOpt[T](thing: T): Option[T] = Some(thing)
  implicit def thingToOptList[T](thing: T): Option[List[T]] = Some(List(thing))

  implicit def stringToHeader(string: String): MdHeader = MdHeader(MdString(string, @@(0,0)), 1, @@(0,0))
  implicit def stringToMdString(string: String): MdString = MdString(string, @@(0,0))
  implicit def stringToContentString(string: String): ContentString = new ContentString(
    List(stringToMdString(string)), "md", ""
  )

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
      List(new ContentString(List(MdString("", @@(0,0))), "")),
      MdHeader(MdString("tagLabel", location), 1, location),
      Nil
    )

    when (mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory(anyString())) thenReturn List(alias, someTag)

    engine.getContentWithQuery("taglabel") shouldBe List(someTag)
  }



  it should "find aliased content" in {
    val alias = new ContentTagAlias("alias", List("taglabel"), @@(0,0), "")

    val string = ContentString(List(MdString("some tag", @@(0,0))), "", "")
    val someTag = new ContentTag(List(string), MdHeader(MdString("taglabel", @@(0,0)), 1, @@(0,0)), Nil, "")

    when(mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory(anyString())) thenReturn List(alias, someTag)

    engine.getContentWithQuery("alias") shouldBe List(someTag)
  }

  it should "find aliased content nested within a tag" in {
    val alias = ContentTagAlias("aliased content", "alias", @@(0,0), "")
    val aliasedTag = ContentTag(ContentString(List(stringToMdString("hippo")), "", ""), "aliased content", Nil, "")
    val tagContainingAlias = ContentTag(ContentString(List(stringToMdString("anything")), "",""), "upper", Nil, "")


    when (mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(tagContainingAlias)

    val query = "upper alias"
    val result = engine.getContentWithQuery(query)

    verify(mockLoader, times(1)).getContentFromDirectory("foo")

    result shouldBe List(aliasedTag)
  }

  "tagMatchesSearchString" should "match an alias with a search string" in {
    val alias = ContentTagAlias("alias", List("label"), @@(0,0), "")

    engine.tagMatchesSearchString(alias, "label") shouldBe true
  }

  it should "match an alias with a tag parent" in {
    val alias = ContentTagAlias("alias", "label", @@(0,0), "")
    val contentTag = ContentTag(List("goo"), "content label", Nil, "")

    engine.tagMatchesSearchString(alias, "content label label") shouldBe true
  }

  it should "match a nested content tag" in {
    val tag1 = ContentTag(List("foo"), "tag1",Nil,"")
    val tag2 = ContentTag(List("foo"), "tag2",Nil,"")

    engine.tagMatchesSearchString(tag1, "tag2 tag1") shouldBe true
  }

  "getAllContentTagLikeThings" should "get content tags and aliases" in {
    val alias = ContentTagAlias("aliased", "alias", @@(0,0), "")
    val aliasedTag = ContentTag(List("hippo"), "hippo", Nil, "")
    val anotherTag = ContentTag(List("giraffe"), "giraffe", Nil, "")

    when (mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(alias, anotherTag, aliasedTag)

    engine.getAllContentTagLikeThings shouldBe List(alias, anotherTag, aliasedTag)
  }

  it should "get nested content including aliases" in {
    val alias = ContentTagAlias("aliased", "alias", @@(0,0), "")
    val aliasedTag = ContentTag(List("hippo"), "hippo", Nil, "")
    val anotherTag = ContentTag(List("giraffe"), "giraffe", Nil, "")

    when (mockIO.getDirectories) thenReturn List("foo")
    when(mockLoader.getContentFromDirectory("foo")) thenReturn List(anotherTag)

    engine.getAllContentTagLikeThings.toSet shouldBe List(alias, anotherTag, aliasedTag).toSet
  }
}
