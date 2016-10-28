package cucumber.steps

import com.gray.parse._

class ParsingSteps extends BaseSteps with ParseConstants {

  var rawString = ""

  var parseResult: Option[List[ParseResult]] = None
  var parser: Option[Parser] = None
  var contentResult: Option[ParseResult] = None

  Before() { scenario =>
    parser = None
    parseResult = None
    contentResult = None
  }

  Given("""^an mdl file exists with one base tag and nested tags$""") { () =>
    rawString = io.Source.fromFile("src/test/resources/fixtures/mdl_notes.txt").mkString
  }

  Given("""^a string exists without tag markings$""") { () =>
    rawString = io.Source.fromFile("src/test/resources/fixtures/unmarked.txt").mkString
  }

  Given("""^a string exists defining a parent visible tag$""") { () =>
    rawString =
      s""">>>$PARENT_VISIBLE_FLAG tag
        |blah blah blah
        |<<<
      """.stripMargin
  }

  When("""^the file is parsed with an mdl parser$""") { () =>
    parser = Some(MdlParser(rawString))
    parseResult = Some(parser.get.parseForResults)
  }

  When("""^we parse the (\d+)(st|nd|rd|th) item of the result$""") { (i: Int, arg0: String) =>
    rawString = parseResult.get(i - 1).string
  }

  When("""^we take the (\d+)(st|nd|rd|th) item of the result$""") { (n: Int, arg: String) =>
    contentResult = Some(parseResult.get(n-1))
  }

  Then("""^A result (is|is not) returned$""") { (isit: String) =>
    parseResult.isDefined mustBe isit.equals("is")
  }

  Then("""^the result contains (\d+) values?$""") { (arg0: Int) =>
    parseResult.get.length mustBe arg0
  }

  Then("""^the (\d+)(st|nd|rd|th) result is a (content tag|content string|content alias)$""") { (index: Int, arg0: String, resultType: String) =>
    val result = parseResult.get(index - 1)
    result.description mustBe (resultType match {
      case "content tag" => CONTENT_TAG
      case "content string" => CONTENT_STRING
      case "content alias" => CONTENT_ALIAS
    })
  }

  Then("""^the result string is equal to the source string$""") { () =>
    parseResult.get.head.string mustBe rawString
  }

  Then("""^the result is a (content tag|content string|content alias)$""") { (resultType: String) =>
    val description = resultType match {
      case "content tag" => CONTENT_TAG
      case "content string" => CONTENT_STRING
      case "content alias" => CONTENT_ALIAS
    }
    contentResult.get.description mustBe description
  }

  Then("""^the result is (universally referenced|parent visible)$""") { (resultType: String) =>
    val description = resultType match {
      case "universally referenced" => PARENT_VISIBLE_FLAG
      case "parent visible" => PARENT_VISIBLE_FLAG
    }
    contentResult.get.options must include (description)
  }


}