package com.gray.note.content_things

import com.gray.markdown.MdLocation
import com.gray.parse.ParseConstants

abstract class Content extends ParseConstants{

  val location: MdLocation

  val path: String

  private var parent : Option[ContentTag] = None

  private [content_things] def setParent(newParent: Option[ContentTag]) = parent = newParent

  def parentTag: Option[ContentTag] = parent

  def getAllDescendantContent: List[Content] = List(this)

  def getString: String

  override def toString: String = getString
}

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
  */
abstract class ContentTagLikeThing extends Content {

  val labels: List[String]

  def getTitleString = labels.headOption.getOrElse("")

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
    *
    * @return
    */
  def isParentVisible: Boolean = false //parseResult.options.contains(PARENT_VISIBLE_FLAG)

  /**
    * Flag specifying whether the tag is referable without specifying its inherited label<br/>
    * e.g.<br/>
    * &emsp;
    *
    * @return
    */
  def isUniversallyReferenced: Boolean = false //parseResult.options.contains(UNIVERSAL_REFERENCE_FLAG)

  def isParaphrase: Boolean

  /**
    * Flag specifying whether the tag is to be visible to the parent tags contents.
    * <ul>
    * <li>if true, the tag will appear in the contents of the parent tag</li>
    * <li>if true, the tag will appear in the autocompletion options</li>
    * <li>if false, the tag may still be searched for AND may appean in the content of it's parent tag if the 'isParentVisible' flag is set to true</li>
    * </ul>
    *
    * @return
    */
  def isContentVisible = true //parseResult.options.contains(CONTENT_INVISIBLE_FLAG)

  def getQueryString : String = {
    if (parentTag.isDefined) s"${parentTag.get.getQueryString} $getTitleString"
    else getTitleString
  }
}








