package com.gray.note.ui

import com.gray.note.content_things.ContentTag
import com.gray.note.handling.SearchEngine

import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.io.AnsiColor

class AutoCompleter(searchEngine: SearchEngine) {

  var currentTag: Option[ContentTag] = None

  private var reset = true

  var tagList = List.empty[ContentTag]

  def updateTagList() = if (reset) {
    tagList = searchEngine.getAllContentTags
    reset = false
  }

  def apply(tag: Option[ContentTag]) = {
    reset = true
    currentTag = tag
  }

  /**
    * Returns a list of strings that are autocompletions for the parameter query.
    *
    * @param string
    */
  def autoComplete(string: String) = {
    updateTagList()

    var result = List.empty[String]

    val possQueries = queryOptions(string)

    possQueries find { tuple =>
      val (query, extension) = tuple
      val prefix = if (currentTag.isDefined) currentTag.get.getQueryString else ""
      val queryWithCurrentTag = s"$prefix $query".replaceAll("\\s+", " ").trim

      var baseList = getTagsForQuery(queryWithCurrentTag).filter(t => t.isContentVisible && t.getTitleString.startsWith(extension))
      if (baseList.isEmpty) baseList = getTagsForQuery(query).filter(t => t.isContentVisible && t.getTitleString.startsWith(extension))

      baseList match {
        case Nil => false
        case list =>
          // I know that query is a match for baseList, and that extension is a partial match for an existing child node
          // list is a list of the partially matching children
          result = list.map(_.getTitleString)
          true
      }
    }
    result
  }

  def getLandingQuery(queries: List[(String, String)]) = queries find { tuple =>
    val (query, extension) = tuple
    val baseList = getTagsForQuery(query)
    baseList.flatMap(_.getTagContents).filter(t => t.isContentVisible && t.getTitleString.startsWith(extension)) match {
      case Nil => false
      case list => true
    }
  }

  def autoCompleteFromHome(string: String) = {
    val option = queryOptions(string)
  }

  /**
    * takes a string representing a query, and returns a list of potential subqueries. e.g. <p/>
    * "this that the other" => "this that the other". "this that the", "this that", "this" <p/>
    * The idea being that given a query, we might think of it as either a query matching a tag to be autocompleted (i.e. look for further options for this tag, or it might already be a partial specification of nested tag. This ambiguity is beacuse tags may have spaces in them (like 'the other'), but spaces are also used to separate tags.
    *
    * @param string
    * @return
    */
  protected [ui] def queryOptions(string: String): List[(String, String)] = {
    if (string.matches("^\\s+$")) return List.empty[(String, String)]

    val extension = if (string.endsWith(" ")) "" else "\\S+$".r.findFirstIn(string).getOrElse("")
    val base = string.stripSuffix(extension).trim

    def recursion(buffer: ListBuffer[(String, String)]): List[(String, String)] = buffer.last match {
      case ("", _) => buffer.toList
      case (str, ectn) =>
        val ex = "\\S+$".r.findFirstIn(str).get
        val newEx = if (ectn == "") ex else ex + " " + ectn
        val newBase = str.stripSuffix(ex).trim
        buffer.+=((newBase, newEx))
        recursion(buffer)
    }
    recursion(ListBuffer((base, extension)))
  }

  protected [ui] def getBaseQueryAndLeftover(string: String) =
    if (string.matches("^\\s*$")) {
      ("", "")
    } else if (string.endsWith(" ")) {
      (string.trim, "")
    } else {
      val lastWord = "\\w+$".r.findFirstIn(string).get
      val query = string.stripSuffix(lastWord).trim
      (query, lastWord)
    }


  def getTagsForQuery(string: String) = if (string != "") {
    searchEngine.getContentWithQuery(string) flatMap (_.getTagContents)
  } else searchEngine.getBaseTags


}
