package cucumber.steps

import com.gray.note.Config
import com.gray.note.content_things.{Content, ContentTag}
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.{FlatSpec, MustMatchers}


class BaseSteps extends FlatSpec with ScalaDsl with EN with MustMatchers {

  val resultHolder = ResultHolder
  Config.environment = Config.TEST

  Before() { s =>
    resultHolder.reset
  }

}

object ResultHolder {
  var contentTags: Option[Array[ContentTag]] = None
  var content: Option[Array[Content]] = None
  var string: Option[String] = None

  var any: Any = Unit

  def reset = {
    contentTags = None
    content = None
    string = None

    any = Unit
  }
}
