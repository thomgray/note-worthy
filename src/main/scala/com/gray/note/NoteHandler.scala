package com.gray.note

import java.io.File

import com.gray.note.content_things.ContentTag
import com.gray.util.IO

import scala.io.Source

class NoteHandler(_filesDirectory: String) {

//  val filesDirectory = _filesDirectory

  val directoriesList = _filesDirectory
  val io = IO(directoriesList)

  var baseTags: Array[ContentTag] = handleBaseTags

  def getAllTags = baseTags flatMap {
    _.getAllDescendantTags
  }

  def getAllRegularTags = for (tag <- getAllTags if !tag.isParaphrase) yield tag

  private def getFilesInNotesDirectory(): Array[File] = {
    var files = Array[File]()
    for (path <- io.getDirectories) {
      files = files ++: new File(path).listFiles.filter(_.getName.endsWith(".txt"))
    }
    files
  }

  private def getContentOfFile(file: File): String = {
    var fileString = ""
    val buffer = Source.fromFile(file)
    for (line <- buffer.getLines()) {
      fileString += line + "\n"
    }
    fileString.trim
  }

  private def handleBaseTags = {
    for {
      file <- getFilesInNotesDirectory()
      rawNote = getContentOfFile(file)
      rootTag = new ContentTag("{" + rawNote + "}", "")
      innerTag <- rootTag.contents
      if innerTag.isInstanceOf[ContentTag]
    } yield {
      innerTag.parentTag = None
      innerTag.asInstanceOf[ContentTag]
    }
  }

  def refreshNotes = baseTags = handleBaseTags

  def tagsMatchingLabel(label: String): Array[ContentTag] = {
    getTagsMatchingLabel(label)
  }

  def getTagsMatchingLabel(label: String) = for (tag <- getAllRegularTags if tagMatchesLabel(tag, label)) yield tag

  private def tagMatchesLabel(tag: ContentTag, label: String): Boolean = {
    val _label = label.toLowerCase()
    if (tag.matchesThisLabel(_label) && (tag.parentTag.isEmpty || tag.isUniversallyReferenced)) true
    else {
      (tag.matchesThisLabelPartially(_label), tag.parentTag) match {
        case (Some(newLabel), Some(parent)) => // there is a partial endwise match for this tag, so check the parent tag
          tagMatchesLabel(parent, newLabel) //TODO this should drill down really. It would rarely be a problem, but could block some aliasing
        case _ => tagMatchesLabelThroughAlias(tag, _label)
      }
    }
  }

  /**
    * the is no endwise match, but perhaps there is an alias for this tag
    * so check the aliases that define this label, and check if that aliased label matches this tag
    *
    * @param tag
    * @param label
    */
  private def tagMatchesLabelThroughAlias(tag: ContentTag, label: String): Boolean = {
    for (alias <- getAllParaphraseTags(label)) {
      if (tagMatchesLabel(tag, alias.getFullPath)) return true
    }
    false
  }

  def getAllParaphraseTags = for (tag <- getAllTags if tag.isParaphrase) yield tag

  def getAllParaphraseTags(label: String): Array[ContentTag] = for {
    tag <- getAllParaphraseTags
    if tag.labels.isDefined
    tagLabels = tag.labels.get
    tagLabel <- tagLabels
    if label endsWith tagLabel
  } yield tag

  //TODO may not be needed - check usages
  def getAllParaphraseSubstitutions: Array[(String, String)] = for {
    tag <- getAllParaphraseTags
    tagContent = tag.contents(0).toString.toLowerCase
    label <- tag.labels.get
    lcLabel = label.toLowerCase()
  } yield tagContent -> lcLabel
}

object NoteHandler {
  def apply(path: String): NoteHandler = new NoteHandler(path)
}

