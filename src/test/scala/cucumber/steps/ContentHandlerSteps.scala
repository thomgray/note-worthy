package cucumber.steps

import java.io.PrintWriter

import com.gray.note.Config
import com.gray.note.content_things.{Content, ContentTag}
import com.gray.note.handling.ContentHandler

class ContentHandlerSteps extends BaseSteps {

  var contentHandler: Option[ContentHandler] = None
  var searchResult: Option[List[ContentTag]] = None

  Before { f =>
    contentHandler = None
    searchResult = None
  }

  Given("""^a resource exists specifying a directory list of foo notes$""") { () =>
    val writer = new PrintWriter(Config.testDirectories)
    writer.write("/Users/grayt13/Projects/note-worthy/src/test/resources/fixtures/mdl_stub_directories/foo_notes/")
    writer.close
  }

  Given("""^a content handler is initialised with a resourceIO pointing to that list$""") { () =>
    contentHandler = Some(ContentHandler(Config.testDirectories))
  }

  Given("""^a resource exists specifying a directory list of notes1$""") { () =>
    val writer = new PrintWriter(Config.testDirectories)
    writer.write("/Users/grayt13/Projects/note-worthy/src/test/resources/fixtures/mdl_stub_directories/test_notes1/")
    writer.close
  }

  When("""^the content handler is called to get all content$""") { () =>
    searchResult = Some(contentHandler.get.getAllContentTags)
  }

  When("""^we search for a tag matching the search string "([^"]*)"$""") { (arg0: String) =>
    searchResult = Some(contentHandler.get.getContentWithQuery(arg0))
  }

  Then("""^all the content in those directories is loaded$""") { () =>
    val content = searchResult.get
    content.length mustBe 5
  }
  Then("""^a single search result is returned$""") { () =>
    searchResult.get.length mustBe 1
  }

  Then("""^the 1st search result is the git config link tag$""") { () =>
    val result = searchResult.get.head
    result.getLabels must contain ("link")
  }

}

