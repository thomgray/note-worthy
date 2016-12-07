package cucumber.steps

import com.gray.note.content_things.ContentTag
import com.gray.parse._
import com.gray.parse.mdlparse.MdlIterator

class ParsingSteps extends BaseSteps with ParseConstants {

  var parser: Option[ParseIterator] = None
  var tag: Option[ContentTag] = None


  Before { scenario =>
    parser = None
    tag = None
  }

  Given("""^an mdl string exists with one base tag and nested tags$""") { () =>
    rawString = io.Source.fromFile("src/test/resources/fixtures/topic_manager_mdl.txt").mkString
  }

  Given("""^an mdl string exists without tag markings$""") { () =>
    rawString = io.Source.fromFile("src/test/resources/fixtures/unmarked.txt").mkString
  }

  Given("""^an mdl string exists defining a parent visible tag$""") { () =>
    rawString =
      s"""[[[$PARENT_VISIBLE_FLAG tag
          |blah blah blah
          |]]]
      """.stripMargin
  }

  Given("""^an mdl string exists defining a universal reference tag$""") { () =>
    rawString =
      s"""[[[$UNIVERSAL_REFERENCE_FLAG tag
          |blah blah
          |]]]
       """.stripMargin
  }

  Given("""^an mdl string exists defining a content invisible tag$""") { () =>
    rawString =
      s"""[[[$CONTENT_INVISIBLE_FLAG tag
          |blah blah
          |]]]
       """.stripMargin
  }

  When("""^the file is parsed with an mdl parser$""") { () =>
    parser = Some(MdlIterator(rawString))
    parseResults = Some(parser.get.iterate)
  }

  When("""^we parse the (\d+)(st|nd|rd|th) item of the result$""") { (i: Int, arg0: String) =>
    rawString = parseResults.get(i - 1).string
  }

  When("""^we take the (\d+)(st|nd|rd|th) item of the result$""") { (n: Int, arg: String) =>
    parseResult = Some(parseResults.get(n - 1))
  }

  Then("""^a result (is|is not) returned$""") { (isit: String) =>
    parseResults.isDefined mustBe isit.equals("is")
  }

  Then("""^the result contains (\d+) values?$""") { (arg0: Int) =>
    parseResults.get.length mustBe arg0
  }

  Then("""^the (\d+)(st|nd|rd|th) result is a (content tag|content string|content alias)$""") { (index: Int, arg0: String, resultType: String) =>
    val result = parseResults.get(index - 1)
    result.description mustBe (resultType match {
      case "content tag" => CONTENT_TAG
      case "content string" => CONTENT_STRING
      case "content alias" => CONTENT_ALIAS
    })
  }

  Then("""^the result string is equal to the source string$""") { () =>
    parseResults.get.head.string mustBe rawString
  }

  Then("""^the result is a (content tag|content string|content alias)$""") { (resultType: String) =>
    val description = resultType match {
      case "content tag" => CONTENT_TAG
      case "content string" => CONTENT_STRING
      case "content alias" => CONTENT_ALIAS
    }
    parseResult.get.description mustBe description
  }

  Then("""^the result is (universally referenced|parent visible|content invisible)$""") { (resultType: String) =>
    val description = resultType match {
      case "universally referenced" => UNIVERSAL_REFERENCE_FLAG
      case "parent visible" => PARENT_VISIBLE_FLAG
      case "content invisible" => CONTENT_INVISIBLE_FLAG
    }
    parseResult.get.options must include(description)
  }

  When("""^a tag is initialised with that result$""") { () =>
    tag = Some(new ContentTag(parseResult.get))
  }

  Then("""^the tag is content invisible$""") { () =>
    tag.get.isContentVisible mustBe false
  }


}