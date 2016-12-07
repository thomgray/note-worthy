package com.gray.note.ui

import org.scalatest.{FlatSpec, MustMatchers}
import com.gray.note.ui.MainController._

class MainControllerSpec extends FlatSpec with MustMatchers {

  "handleArgs()" should "find the help command" in {
    handleArgs("--help")._2 mustBe Map(HELP_KEY -> Seq.empty)
    handleArgs("-h")._2 mustBe Map(HELP_KEY -> Seq.empty)
  }

  it should "find the quit command" in {
    handleArgs("--quit")._2 mustBe Map(EXIT_KEY -> Seq.empty)
    handleArgs("-q")._2 mustBe Map(EXIT_KEY -> Seq.empty)
    handleArgs("--exit")._2 mustBe Map(EXIT_KEY -> Seq.empty)
    handleArgs("-e")._2 mustBe Map(EXIT_KEY -> Seq.empty)
  }

  it should "ignore unrecognised commands" in {
    val (query, args) = handleArgs("--this is a string")
    query mustBe "is a string"
    args mustBe Map.empty
  }

}
