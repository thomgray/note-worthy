package cucumber.steps

import java.io.{File, PrintWriter}

import com.gray.note.Config
import com.gray.note.handling.SearchEngine
import com.gray.note.ui.AutoCompleter
import cucumber.api.scala.ScalaDsl

class AutocompleteSteps extends BaseSteps with ScalaDsl {

  Before { f =>
    val writer = new PrintWriter(new File("file_path"))
    writer.write("/Users/grayt13/Projects/note-worthy/src/test/resources/fixtures/mdl_stub_directories/test_notes1")
  }

  var searchEngine: SearchEngine = _
  var autoCompleter: AutoCompleter = _
  var autoCompleteResult: List[String] = _

  Given("""^a search engine exists that points to a resource directory$"""){ () =>
    searchEngine = SearchEngine(Config.testRootDirectory)
  }

  Given("""^an autocompleter exists with that search engine$"""){ () =>
    autoCompleter =  new AutoCompleter(searchEngine)
  }

  When("""^I autocomplete "([^"]*)"$"""){ (arg0:String) =>
    autoCompleteResult = autoCompleter.autoComplete(arg0)
  }

  Then("""^I receive a list with (\d+) item$"""){ (arg0:Int) =>
    autoCompleteResult.length mustBe arg0
  }

  Then("""^I get receive a list containing "([^"]*)"$"""){ (arg0:String) =>
    autoCompleteResult must contain(arg0)
  }

}
