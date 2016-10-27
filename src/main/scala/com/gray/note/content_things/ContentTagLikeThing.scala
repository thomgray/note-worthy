package com.gray.note.content_things

import com.gray.note.parsing.TagParser
import com.gray.note.{AttributedString, Config}

import scala.collection.mutable.ArrayBuffer

/**
  * An abstract class that defines a thing with the following functionality:
  * <ul>
  *   <li>Is tag-able: so specifies a number of labels that link to the content. These are captured in the headString in the form @(label-1;label-2..;label-n)
  *     <ul>
  *       <li>getLabels = ??? should return a list of these</li>
  *       <li>getTitleString = ??? should return a string title for this tag, usually the first label</li>
  *     </ul>
  *   </li>
  *   <li>Has some form of content, i.e. the stuff which is being tagged. This is captured in the bodyString. This can be in the form of further tags or content strings or just a scala string. There is no promise being made in this class how that content is to be modeled (it may not want to be visible, as is the case for the ContentAliasTag).
  *     <ul>
  *       <li>def getNeatBody = ??? should return a 'neat' string representation of the body</li>
  *   </li>
  * </ul>
  * @param bodyString
  * @param headString
  */
abstract class ContentTagLikeThing(bodyString: String, headString: String) extends Content(bodyString) {

  def getLabels: Option[Array[String]] = ???

  def getTitleString: String = ???

  /**
    * Flag specifying if the tag is visible within the BODY of the parent tag<br/>
    * If true, the parent tag may look like this when printed:
    * <p>
    * blah blah<br/>
    * <p>
    * THIS TAG:<br/>
    * &emsp; This tag content blah blah<br/>
    * <p>
    * more parent content
    * @return
    */
  def isParentVisible: Boolean = ???

  /**
    * Flag specifying whether the tag is referable without specifying its inherited label<br/>
    * e.g.<br/>
    * &emsp;
    *
    * @return
    */
  def isUniversallyReferenced: Boolean = ???

  def isParaphrase: Boolean = ???

  def getNeatBody: String = ???

  /**
    * Flag specifying whether the tag is to be visible to the parent tags contents.
    * <ul>
    *   <li>if true, the tag will appear in the contents of the parent tag</li>
    *   <li>if true, the tag will appear in the autocompletion options</li>
    *   <li>if false, the tag may still be searched for AND may appean in the content of it's parent tag if the 'isParentVisible' flag is set to true</li>
    * </ul>
    * @return
    */
  def isContentVisible = true //TODO implement somehow
//
//  private def handleHeader: Option[Array[String]] = {
//    val head = headString.trim.stripPrefix("@(").stripSuffix(")").trim
//    if (head.length == 0) return None
//
//    val buffer = new ArrayBuffer[String]()
//    buffer.++=:(head.split(";"))
//
//    var first = buffer(0)
//    while (buffer.nonEmpty && (first(0) match {
//      case '^' =>
//        _isParentVisible = true
//        first = first.substring(1)
//        true
//      case '*' =>
//        _isUniversallyReferenced = true
//        first = first.substring(1)
//        true
//      case _ => false
//    })) {}
//
//    if (first.length > 0) {
//      buffer(0) = first
//    } else buffer.remove(0)
//
//    if (buffer.isEmpty) None else Some(buffer.toArray[String])
//  }
//
//  private def handleContents = {
//    var rawContent = bodyString.trim
//    rawContent(0) match {
//      case '[' => _isParaphrase = true
//      case _ => _isParaphrase = false
//    }
//    rawContent = rawContent.substring(1, rawContent.length - 1)
//    val _contents = TagParser.getContent(rawContent)
//    for (contentTag <- _contents if contentTag.isInstanceOf[ContentTagLikeThing]){
//      contentTag.parentTag = Some(this)
//    }
//    _contents
//  }
//
//  override def getAllDescendantContent: Array[Content] = {
//    val buffer = new ArrayBuffer[Content]()
//    buffer += this
//    for (content <- this.contents) {
//      buffer.++=(content.getAllDescendantContent)
//    }
//    buffer.toArray
//  }
//
//  def getAllDescendantTags: Array[ContentTagLikeThing] = {
//    val buffer = new ArrayBuffer[ContentTagLikeThing]()
//    buffer += this
//    for (content <- this.contents; if content.isInstanceOf[ContentTag]) {
//      buffer.++=(content.asInstanceOf[ContentTagLikeThing].getAllDescendantTags)
//    }
//    buffer.toArray
//  }
//
//  def getTagsInContents = for (tag <- getTagContents if tag.isParentVisible) yield tag
//
//  /**
//    * Returns an array of all content tags
//    * @return
//    */
//  def getTagContents: Array[ContentTag] =
//  for (content <- contents if content.isInstanceOf[ContentTag]) yield content.asInstanceOf[ContentTag]
//
//  /**
//    * Returns an array of all contenet strings
//    * @return
//    */
//  def getStringContents: Array[ContentString] =
//  for (content <- contents if content.isInstanceOf[ContentString]) yield content.asInstanceOf[ContentString]
//
//
//  def getFullPath: String = {
//    if (this.labels.isEmpty) ""
//    else (parentTag, isParaphrase) match {
//      case (None, false) => this.labels.get(0).toLowerCase
//      case (Some(parent), false) => parent.getFullPath +" "+ this.labels.get(0).toLowerCase
//      case (None, true) => this.getTrimmedBody.toLowerCase
//      case (Some(parent), true)  =>
//        if (_isParentVisible) parent.getFullPath + " " + this.getTrimmedBody.toLowerCase
//        else this.getTrimmedBody.toLowerCase
//    }
//  }
//
//  override def toString: String = {
//    getFormattedString()
//  }
//
//  def getFormattedString(indent: Int = 0) = {
//    var str = ""
//    for (content <- contents) {
//      content match {
//        case contentString: ContentString =>
//          str = str + contentString.formatAndIndentString(indent) + "\n"
//        case tag: ContentTag if content.asInstanceOf[ContentTag].isParentVisible =>
////          val label = tag.getTitleString
//          val newLineIfNeeded = if (str.length>0) "\n" else ""
//          str = str + newLineIfNeeded + BOLD + tag.getTitleString.toUpperCase + RESET + ":\n"
//          str = str + tag.formatAndIndentString(indent+1) + "\n"
//        case _ =>
//      }
//    }
//    str
//  }
//
//  override def getAttributesString = {
//    val hasVisibleTags = (for (tag <- getTagContents if tag.isParentVisible) yield true).length>0
//    val hasStringContent = getStringContents.length>0
//
//    (hasStringContent, hasVisibleTags) match {
//      case (false, false) => new AttributedString(getIndexString)
//      case _ => new AttributedString(getFormattedString(0))
//    }
//  }
//
//  def getIndexString = {
//    var outstr = "Index:\n"
//    for (tag <- getTagContents){
//      outstr = outstr + s"${Config.standardTab}-" + tag.labels.get(0) + "\n"
//    }
//    outstr
//  }
//
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
//
//  /**
//    * Returns true if the specified label is a whole match for either of the tag label options
//    * @param string
//    * @return
//    */
//  def matchesThisLabel(string: String): Boolean = {
//    labels match {
//      case None => false
//      case Some(labs) => labs.map({lab=> lab.trim.toLowerCase}).contains(string)
//    }
//  }
//
//  /**
//    * Returns the remainder of the label (trimmed) if there is a partial end-wise match with one of the labe options of this tag
//    * @param string
//    * @return
//    */
//  def matchesThisLabelPartially(string: String): Option[String] = {
//    labels match {
//      case None => None
//      case Some(labs) =>
//        val cleanLabels = labs.map({lab => lab.trim.toLowerCase})
//        for (lab <- cleanLabels if string.endsWith(lab)) {
//          return Some(string.substring(0, string.length-lab.length).trim)
//        }
//        None
//    }
//  }
//
//  /**
//    * Ignores inner tags! Called by getFormattedString
//    * @param indent
//    * @return
//    */
//  override def formatAndIndentString(indent: Int): String = {
//    var str = ""
//    for (content <- contents) {
//      content match {
//        case contentString: ContentString =>
//          str = str + contentString.formatAndIndentString(indent) + "\n"
//        case _ =>
//      }
//    }
//    str
//  }
//
//  def getIndentedAttributedString(indent: Int) = {
//    var str = ""
//    for (content <- contents) {
//      content match {
//        case contentString: ContentString =>
//          str = str + contentString.formatAndIndentString(indent) + "\n"
//        case _ =>
//      }
//    }
//    str
//  }
}
