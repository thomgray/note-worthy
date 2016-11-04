package cucumber.steps

import com.gray.note.content_things._
import com.gray.parse.ParseConstants

class ContentLoaderSteps extends BaseSteps with ParseConstants {

  val mdlLoader = MdlLoader
  var loaderResults: Option[List[Content]] = None
  var loaderResult: Option[Content] = None
  var filePath: Option[String] = None
  var directoryPath: Option[String] = None

  Before { f =>
    loaderResults = None
    loaderResult = None
    filePath = None
    directoryPath = None
  }

  Given("""^an mdl string exists with one base tag and nested tags to be loaded$""") { () =>
    rawString = io.Source.fromFile("src/test/resources/fixtures/topic_manager_mdl.txt").mkString
  }

  When("""^the string is loaded with an mdl content loader$""") { () =>
    loaderResults = Some(mdlLoader.getContent(rawString))
  }

  When("""^we take the contents of the content tag$""") { () =>
    val res = loaderResult.get.asInstanceOf[ContentTag]
    loaderResults = Some(res.getContents)
  }

  Then("""^the content loader result contains (\d+) values?$""") { (length: Int) =>
    loaderResults.isDefined mustBe true
    loaderResults.get.length mustBe length
  }

  Then("""^the (\d+)(st|nd|rd|th) content loader result is a (content tag)$""") { (index: Int, arg1: String, desc: String) =>
    val res = loaderResults.get(index - 1)
    loaderResult = Some(res)
    res mustBe (desc match {
      case "content tag" => a[ContentTag]
      case "content string" => a[ContentString]
      case "content alias" => a[ContentTagAlias]
    })
  }

  Given("""^a mdl file exists with (\d+) content tags$""") { (arg0: Int) =>
    arg0 match {
      case 3 =>
        filePath = Some("src/test/resources/fixtures/mdl_stub_directories/foo_notes/foo.txt")
      case _ =>
    }
  }

  When("""^the file is loaded with an mdl loader$""") { () =>
    loaderResults = Some(mdlLoader.getContentFromFile(filePath.get))
  }

  Then("""^the content loader loads a result$""") { () =>
    loaderResults.isDefined mustBe true
  }

  Given("""^a directory exists containing mdl notes$""") { () =>
    directoryPath = Some("src/test/resources/fixtures/mdl_stub_directories/foo_notes/")
  }

  When("""^content is loaded from the directory with an mdl loader$""") { () =>
    loaderResults = Some(mdlLoader.getContentFromDirectory(directoryPath.get))
  }


}
