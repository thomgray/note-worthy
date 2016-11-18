package cucumber.steps

import com.gray.note.Config
import com.gray.note.content_things.{Content, ContentTag}
import com.gray.parse.ParseResult
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.{FlatSpec, MustMatchers}


class BaseSteps extends FlatSpec with ScalaDsl with EN with MustMatchers {

  var rawString: String = ""
  var parseResults: Option[List[ParseResult]] = None
  var parseResult: Option[ParseResult] = None


  Before { f =>
    rawString = ""
    parseResults = None
    parseResult = None
  }

}