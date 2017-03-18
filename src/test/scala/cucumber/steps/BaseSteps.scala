package cucumber.steps

import com.gray.parse.AbstractParseResult
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.{FlatSpec, MustMatchers}


class BaseSteps extends FlatSpec with ScalaDsl with EN with MustMatchers {

  var rawString: String = ""
  var parseResults: Option[List[AbstractParseResult]] = None
  var parseResult: Option[AbstractParseResult] = None


  Before { f =>
    rawString = ""
    parseResults = None
    parseResult = None
  }

}
