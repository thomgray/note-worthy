package cucumber.steps

import com.gray.util.Ranj
import com.gray.util.attributed_string.{Attribute, AttributeValidator, AttributedString}
import org.scalatest.MustMatchers

import scala.io.AnsiColor
import scala.util.matching.Regex

class AttributedStringSteps extends BaseSteps with MustMatchers with AnsiColor {

  val attributedString12Homo = AttributedString("twelve chars", Seq(RED))

  val attributedString1 = AttributedString("Hello", Seq(RED, BLACK_B))
  val attributedString2 = AttributedString(" World!", Seq(BLUE))
  val attributedString3 = new AttributedString("This is a rather pointless sentence",
    List(Attribute(Ranj(0, 7), Seq(RED)), Attribute(Ranj(17, 26), Seq(RED_B))))

  var atString1: Option[AttributedString] = None

  def getAtString1 = atString1.get

  var atString2: Option[AttributedString] = None

  def getAtString2 = atString2.get

  var resultString: Option[AttributedString] = None

  def getResultString = resultString.get

  var atStrRegex: Option[Regex] = None

  def getRegex = atStrRegex.get

  def getAttribute(string: String) = string match {
    case "RED" => RED
    case "BLUE" => BLUE
    case "BLACK" => BLACK
    case "CYAN" => CYAN
    case "YELLOW" => YELLOW
    case "RED_B"=> RED_B
    case "BLUE_B"=> BLUE_B
    case "BLACK_B"=> BLACK_B
    case "CYAN_B"=> CYAN_B
    case "YELLOW_B"=> YELLOW_B
   }

  Before { f =>
    resultString = None
    atString1 = None
    atString2 = None
    atStrRegex = None
  }

  Given("""^an attributed string exists with a single, homogenous attribute$""") { () =>
    atString1 = Some(attributedString1)
  }

  When("""^the string is concatenated with another homogenous attributed string$""") { () =>
    atString2 = Some(attributedString2)
    resultString = Some(atString1.get + atString2.get)
  }

  Then("""^the length of the result string is equal to the sum of the lenghts of the other strings$""") { () =>
    resultString.get.length mustBe atString1.get.length + atString2.get.length
  }

  Then("""^the string has two attributes spanning the length of the string$""") { () =>
    resultString.get.attributes.length mustBe 2
    val atts = resultString.get.attributes.toList
    atts(0).range mustBe Ranj(0, 5)
    atts(0).formatString mustBe RED + BLACK_B
    atts(1).range mustBe Ranj(5, 12)
    atts(1).formatString mustBe BLUE
  }

  Given("""^a long attributed string exists with several attributes$""") { () =>
    atString1 = Some(attributedString3)
  }

  Given("""^a regex is specified that mas a match within the string$""") { () =>
    atStrRegex = Some("rather pointless *".r)
  }

  When("""^the string is regexed for a match$""") { () =>
    resultString = getAtString1.findFirst(getRegex)
  }

  Then("""^an attributed string is returned$""") { () =>
    resultString.isDefined mustBe true
  }

  Then("""^the result matches the regex$""") { () =>
    getResultString.string.matches(getRegex.toString()) mustBe true
  }

  Then("""^The attributes in the result string are preserved from the original$""") { () =>
    getResultString.attributes.length mustBe 1
    val resAt = getResultString.attributes.head
    resAt.range mustBe Ranj(7, 16)
  }

  When("""^a substring is extracted from the string$""") { () =>
    resultString = Some(getAtString1.substring(5, 19))
  }
  Then("""^the result string matches the substring of the original$""") { () =>
    getResultString.string mustBe "is a rather po"
  }

  Then("""^the attributes match the attributes of the original$""") { () =>
    val ats = getResultString.attributes
    ats.length mustBe 2
    ats.toList(0).range mustBe Ranj(0, 2)
    ats.toList(1).range mustBe Ranj(12, 14)
  }

  Given("""^a (\w+) attributed string exists of length (\d+)$""") { (attribute: String, arg0: Int) =>
    val format = getAttribute(attribute)
    val string = arg0 match {
      case 12 => "twelve chars"
    }
    atString1 = Some(AttributedString(string, Seq(format)))
  }

  When("""^an attribute (\w+) is added to the string in range (\d+) - (\d+)$""") { (attributeFormat: String, start: Int, end: Int) =>
    val format = getAttribute(attributeFormat)
    resultString = Some(getAtString1.addAttribute(Seq(format), Ranj(start, end)))
  }

  Then("""^the result string has (\d+) attributes$""") { (arg0: Int) =>
    getResultString.attributes.length mustBe arg0
  }

  Then("""^the (\d+)(st|nd|rd|th) attribute of the result is in range (\d+) - (\d+)$""") { (index: Int, arg: String, start: Int, end: Int) =>
    getResultString.attributes.toList(index - 1).range mustBe Ranj(start, end)
  }

  Then("""^the (\d+)(st|nd|rd|th) attribute of the result is "([^"]*)"$"""){ (index:Int, arg0: String, arg1:String) =>
    val atsList = arg1.split("\\s").map(getAttribute)
    getResultString.attributes.toList(index-1).formats.toList mustBe atsList
  }


  Given("""^a (\w+) attributed string exists with text "([^"]*)"$""") { (format: String, text: String) =>
    val _format = getAttribute(format)
    atString1 = Some(AttributedString(text, Seq(_format)))
  }

  Given("""the string has a (\w+) attribute in range (\d+) - (\d+)""") { (att: String, start: Int, end: Int) =>
    val at = getAttribute(att)
    atString1 = Some(getAtString1.addAttribute(Seq(at), Ranj(start, end)))
  }

  When("""^(\w+) attributes are added to the string with a regex "([^"]*)"$""") { (at: String, arg0: String) =>
    val regex = arg0.r
    val attribute = getAttribute(at)
    resultString = Some(getAtString1.addAttributeForRegex(regex, Seq(attribute)))
  }

  Then("""^the result string attributes do not overlap$""") { () =>
    AttributeValidator.validateAttributedString(getResultString)
  }

  Given("""^a regex is specified matching (\d+) groups in the string$"""){ (arg0:Int) =>
    atStrRegex = Some("(is).*(text?)".r)
  }

  When("""^different attributes are added to the regex groups$"""){ () =>
    val groupMap = Map(1 -> Seq(RED), 2 -> Seq(BOLD))
    resultString = Some(getAtString1.addAttributeForRegexGroups(getRegex, groupMap))
  }


}
