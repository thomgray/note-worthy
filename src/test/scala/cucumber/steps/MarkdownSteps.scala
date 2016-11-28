package cucumber.steps

import com.gray.markdown.parsing.MdParser
import com.gray.markdown.{MdLink, MdParagraph, MdString}
import cucumber.api.PendingException


class MarkdownSteps extends BaseSteps {

  private def getInlineLink(string: String) = string match {
    case "google" => "[google](https://www.google.co.uk)"
    case "github" => "www.github.com"
    case _ => throw new PendingException()
  }

  private def getMdLink(string: String) = string match {
    case "google" => MdLink("https://www.google.co.uk", Some("google"))
    case "githum" => MdLink("www.github.com")
    case _ => throw new PendingException()
  }

  import MarkdownStepsHolder._

  Before { f =>
    reset
  }

  Given("""^a md document exists with a single string$"""){ () =>
    setDocumentRaw(
      """
        |this is just a string with not mich going on
      """.stripMargin)
  }
  When("""^the document is parsed$"""){ () =>
    setParhraphs(MdParser.parse(documentRaw()))
  }

  Then("""^the result contains (\d+) paragraphs?$"""){ (arg0:Int) =>
    paragraphs.length mustBe arg0
  }

  Given("""^a md document exists with a single string containing a plain link$"""){ () =>
    setDocumentRaw(
      """
        |this is a string with a link: www.google.com
      """.stripMargin)
  }

  Then("""^the (\d+)(?:st|nd|rd|th) paragraph is a (string?)$""") { (parNum: Int, tipo: String) =>
    val pg = paragraph(parNum-1)

    tipo match {
      case "string" => assertType[MdString]
      case _ => throw new PendingException()
    }

    def assertType[T <: MdParagraph] = pg.isInstanceOf[T] mustBe true
  }

  Then("""^the (\d+)(?:st|nd|rd|th) paragraph contains (\d+) (links?)$"""){ (parNum:Int, typeNum:Int, tipo:String) =>
    val str = paragraph(parNum-1).asInstanceOf[MdString]

    val things = tipo match {
      case "link" | "links" => str.links
    }

    things.length mustBe typeNum
  }

  Given("""^a md document exists with a "([^"]*)" ref link and a "([^"]*)" link$"""){ (arg0:String, arg1:String) =>
    val link1 = getInlineLink(arg0)
    val link2 = getInlineLink(arg1)

    setDocumentRaw(s"this is a document with a link here: $link1 and another link here: $link2")
  }


  When("""^we take the links in the (\d+)(?:st|nd|rd|th) paragraph$"""){ (arg0:Int) =>
    setLinks(paragraphs(arg0-1).asInstanceOf[MdString].links.map(_._1))
  }

  Then("""^there are (\d+) links$"""){ (arg0:Int) =>
    links.length mustBe arg0
  }

  Then("""^the (\d+)(?:st|nd|rd|th) link is "([^"]*)"$"""){ (arg0:Int, arg1:String) =>
    val resLink = link(arg0)
    val expected = getMdLink(arg1)
    resLink mustBe expected
  }



}

object MarkdownStepsHolder {

  var _documentRaw: Seq[String] = _

  def documentRaw(i: Int = 0) = _documentRaw(i)
  def setDocumentRaw(string: String *) = _documentRaw = string

  var _stringMd: Seq[MdString] = _
  def stringMd(int: Int = 0) = _stringMd(int)
  def setStringMd(mdString: MdString *) = _stringMd = mdString

  var _paragraphs: List[MdParagraph] = _
  def paragraph(int: Int = 0) = _paragraphs(int)
  def paragraphs =_paragraphs
  def setParhraphs(pars: List[MdParagraph]) = _paragraphs = pars

  var _links: List[MdLink] = _
  def links = _links
  def link(int: Int = 0) = _links(int)
  def setLinks(links: List[MdLink]) = _links = links

  def reset = {
    _documentRaw = null
    _stringMd = null
    _paragraphs = null
    _links = null
  }
}
