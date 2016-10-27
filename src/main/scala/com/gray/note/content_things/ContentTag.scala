package com.gray.note.content_things

import com.gray.note.{AttributedString, Config}
import com.gray.note.parsing.TagParser

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

class ContentTag(bodyString: String, headString: String) extends ContentTagLikeThing(bodyString, headString) {
  private var _isParentVisible = false
  private var _isUniversallyReferenced = false
  private var _isParaphrase = false

  val labels: Option[Array[String]] = handleHeader
  val contents = handleContents

  def getContents = contents

  override def getLabels = labels

  override def getTitleString = if (labels.isDefined && !labels.get.isEmpty) labels.get(0) else ""

  override def getNeatBody = {
    val trim1 = bodyString.trim
    trim1.substring(1, trim1.length-1).trim
  }

  override def isParentVisible = _isParentVisible

  override  def isUniversallyReferenced = _isUniversallyReferenced

  override def isParaphrase = _isParaphrase //should always be false!

  override def isContentVisible = true //TODO implement somehow

  private def handleHeader: Option[Array[String]] = {
    val head = headString.trim.stripPrefix("@(").stripSuffix(")").trim
    if (head.length == 0) return None

    val buffer = new ArrayBuffer[String]()
    buffer.++=:(head.split(";"))

    var first = buffer(0)
    while (buffer.nonEmpty && (first(0) match {
      case '^' =>
        _isParentVisible = true
        first = first.substring(1)
        true
      case '*' =>
        _isUniversallyReferenced = true
        first = first.substring(1)
        true
      case _ => false
    })) {}

    if (first.length > 0) {
      buffer(0) = first
    } else buffer.remove(0)

    if (buffer.isEmpty) None else Some(buffer.toArray[String])
  }

  private def handleContents: Array[Content] = {
    var rawContent = bodyString.trim
    rawContent(0) match {
      case '[' => _isParaphrase = true
      case _ => _isParaphrase = false
    }
    rawContent = rawContent.substring(1, rawContent.length - 1)
    val _contents = TagParser.getContent(rawContent)
    for (contentTag <- _contents if contentTag.isInstanceOf[ContentTag]){
      contentTag.asInstanceOf[ContentTag].parentTag = Some(this)
    }
    _contents
  }

  override def getAllDescendantContent: Array[Content] = {
    val buffer = new ArrayBuffer[Content]()
    buffer += this
    for (content <- this.contents) {
      buffer.++=(content.getAllDescendantContent)
    }
    buffer.toArray
  }

  def getAllDescendantTags: Array[ContentTag] = {
    val buffer = new ArrayBuffer[ContentTag]()
    buffer += this
    for (content <- this.contents; if content.isInstanceOf[ContentTag]) {
      buffer.++=(content.asInstanceOf[ContentTag].getAllDescendantTags)
    }
    buffer.toArray
  }

  def getTagsInContents = for (tag <- getTagContents if tag.isParentVisible) yield tag

  /**
    * Returns an array of all content tags
    * @return
    */
  def getTagContents: Array[ContentTag] =
  for (content <- contents if content.isInstanceOf[ContentTag]) yield content.asInstanceOf[ContentTag]

  /**
    * Returns an array of all contenet strings
    * @return
    */
  def getStringContents: Array[ContentString] =
  for (content <- contents if content.isInstanceOf[ContentString]) yield content.asInstanceOf[ContentString]



  def getFullPath: String = {
    if (this.labels.isEmpty) ""
    else (parentTag, isParaphrase) match {
      case (None, false) => this.labels.get(0).toLowerCase
      case (Some(parent), false) => parent.getFullPath +" "+ this.labels.get(0).toLowerCase
      case (None, true) => this.getNeatBody.toLowerCase
      case (Some(parent), true)  =>
        if (_isParentVisible) parent.getFullPath + " " + this.getNeatBody.toLowerCase
        else this.getNeatBody.toLowerCase
    }
  }

  override def toString: String = {
    getFormattedString()
  }

  def getFormattedString(indent: Int = 0) = {
    var str = ""
    for (content <- contents) {
      content match {
        case contentString: ContentString =>
          str = str + contentString.formatAndIndentString(indent) + "\n"
        case tag: ContentTag if content.asInstanceOf[ContentTag].isParentVisible =>
//          val label = tag.getTitleString
          val newLineIfNeeded = if (str.length>0) "\n" else ""
          str = str + newLineIfNeeded + BOLD + tag.getTitleString.toUpperCase + RESET + ":\n"
          str = str + tag.formatAndIndentString(indent+1) + "\n"
        case _ =>
      }
    }
    str
  }

  override def getAttributesString = {
    val hasVisibleTags = (for (tag <- getTagContents if tag.isParentVisible) yield true).length>0
    val hasStringContent = getStringContents.length>0

    (hasStringContent, hasVisibleTags) match {
      case (false, false) => new AttributedString(getIndexString)
      case _ => new AttributedString(getFormattedString(0))
    }
  }

  def getIndexString = {
    var outstr = "Index:\n"
    for (tag <- getTagContents){
      outstr = outstr + s"${Config.standardTab}-" + tag.labels.get(0) + "\n"
    }
    outstr
  }

//  def matchesLabel(label: String): Boolean = {
//    if (labels.isEmpty) return false
//    val workingLabel = label.trim
//    for (thisLabel <- labels.get) {
//      if (workingLabel.equals(thisLabel.toLowerCase)) {
//        if (parentTag.isEmpty || _isUniversallyReferenced) return true
//      }else if (workingLabel.endsWith(thisLabel.toLowerCase) && parentTag.isDefined) {
//        val passedLabel = workingLabel.substring(0, workingLabel.length-thisLabel.length)
//        if (parentTag.get.matchesLabel(passedLabel)) return true
//      }
//    }
//    false
//  }

  /**
    * Returns true if the specified label is a whole match for either of the tag label options
    * @param string
    * @return
    */
  def matchesThisLabel(string: String): Boolean = {
    matchesThisLabelPartially(string).getOrElse("not").isEmpty
//    labels match {
//      case None => false
//      case Some(labs) => labs.map({lab=> lab.trim.toLowerCase}).contains(string)
//    }
  }

  /**
    * Returns the remainder of the label (trimmed) if there is a partial end-wise match with one of the label options of this tag
    * @param string
    * @return
    */
  def matchesThisLabelPartially(string: String): Option[String] = {
    labels match {
      case Some(labs) if !labs.isEmpty =>
        val cleanLabels = labs.map({lab => lab.trim.toLowerCase})
        labs.collectFirst{
          case label if label.startsWith("\"") && label.endsWith("\"") && partiallyMatchesRegex(string, label).isDefined =>
            partiallyMatchesRegex(string, label).get
          case label if string.endsWith(label) => string.substring(0, string.length-label.length).trim
        }
      case None => None
    }
  }

  private def partiallyMatchesRegex(searchString: String, regexString: String): Option[String] ={
    val regexString2 = regexString.stripPrefix("\"").stripSuffix("\"")
    val regex = if (regexString2.endsWith("$")) regexString2.r else regexString2.concat("$").r
    regex.findFirstIn(searchString) match {
      case Some(regMatch) => Some(searchString.stripSuffix(regMatch).trim)
      case None => None
    }
  }

  /**
    * Ignores inner tags! Called by getFormattedString
    * @param indent
    * @return
    */
  override def formatAndIndentString(indent: Int): String = {
    var str = ""
    for (content <- contents) {
      content match {
        case contentString: ContentString =>
          str = str + contentString.formatAndIndentString(indent) + "\n"
        case _ =>
      }
    }
    str
  }

  def getIndentedAttributedString(indent: Int) = {
    var str = ""
    for (content <- contents) {
      content match {
        case contentString: ContentString =>
          str = str + contentString.formatAndIndentString(indent) + "\n"
        case _ =>
      }
    }
    str
  }
}