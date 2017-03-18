package com.gray.note.ui

import com.gray.note.content_things.{ContentLoader, ContentTag, MdlLoader}
import com.gray.note.handling.SearchEngine
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import org.mockito.Mockito._


class AutoCompleterSpec extends FlatSpec with Matchers with BeforeAndAfter with MockitoSugar{

  val stubSearchEngine = mock[SearchEngine]

  before {
    reset(stubSearchEngine)
  }

  "queryOptions" should "find a list of possible queries given a string" in {
    val str = "this that the other"
    val possQueries = new AutoCompleter(stubSearchEngine).queryOptions(str)
    possQueries shouldBe List(
      "this that the" -> "other",
      "this that" -> "the other",
      "this" -> "that the other",
      "" -> "this that the other"
    )
  }

  it should "find a list of queries when the string ends with a space" in {
    val str = "this that the other "
    val possQueries = new AutoCompleter(stubSearchEngine).queryOptions(str)
    possQueries shouldBe List(
      "this that the other" -> "",
      "this that the" -> "other",
      "this that" -> "the other",
      "this" -> "that the other",
      "" -> "this that the other"
    )
  }

  it should "return an empty list if a blank string is supplied" in {
    new AutoCompleter(stubSearchEngine).queryOptions("  ") should be (Nil)
  }

  it should "work if a partially specified query only is provided" in {
    new AutoCompleter(stubSearchEngine).queryOptions("this") should be (List("" -> "this"))
  }

  "getBaseQueryAndLeftover()" should "split find a whole query if it ends with a whitespace" in {
    val query = "this that the other "
    val (base, leftover) = new AutoCompleter(stubSearchEngine).getBaseQueryAndLeftover(query)
    base should be ("this that the other")
    leftover should be ("")
  }

  it should "find a partial query if the string doesn't end with a white space" in {
    val query = "this that the oth"
    val (base, leftover) = new AutoCompleter(stubSearchEngine).getBaseQueryAndLeftover(query)
    base should be ("this that the")
    leftover should be ("oth")
  }

  it should "succeed with blanks for a blank query" in {
    val query = ""
    val (base, leftover) = new AutoCompleter(stubSearchEngine).getBaseQueryAndLeftover(query)
    base should be ("")
    leftover should be ("")
  }

  it should "succeed with blanks for a blank query with whitespace" in {
    val query = "  "
    val (base, leftover) = new AutoCompleter(stubSearchEngine).getBaseQueryAndLeftover(query)
    base should be ("")
    leftover should be ("")
  }

  "getTagsForQuery()" should "return a list of child nodes for a tag" in {
    val str =
      """[[[label1
        |
        | [[[tag1
        | sdfsdf
        | ]]]
        | [[[tag2
        | sdfsd
        | ]]]
        | [[[tag3
        | sdfs
        | ]]]
        |
        |]]]
      """.stripMargin
    val tags = MdlLoader.getContent(str, "").asInstanceOf[List[ContentTag]]

    when (stubSearchEngine.getContentWithQuery("label1")) thenReturn tags

    val things = new AutoCompleter(stubSearchEngine).getTagsForQuery("label1")

    things should be (tags(0).getTagContents)
  }



}
