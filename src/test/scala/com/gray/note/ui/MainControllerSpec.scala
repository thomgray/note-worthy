package com.gray.note.ui

import org.scalatest.{FlatSpec, MustMatchers}
import com.gray.note.ui.MainController._

class MainControllerSpec extends FlatSpec with MustMatchers {

  "handleArgs()" should "find the help command" in {
    handleArgs("--help")._2 mustBe Map(HELP_KEY -> Seq.empty)
    handleArgs("-h")._2 mustBe Map(HELP_KEY -> Seq.empty)
  }

}
