package com.gray.note.util.attributed_string

import scala.io.AnsiColor

case class Formats(foregroundColor: Option[String], backgroundColor: Option[String], otherFormats: Seq[String]){
  def toList = List(foregroundColor, backgroundColor).flatten ++ otherFormats
  override def toString: String = toList.map(Formats.nameForFormat).mkString(",")
  def formatString = toList.mkString

}

object Formats extends AnsiColor{

//  def apply(foregroundColor: Option[String], backgroundColor: Option[String], otherFormats: Seq[String]): Formats = new Formats(foregroundColor, backgroundColor, otherFormats)

  def apply(formats: Seq[String]): Formats = {
    val foregroundColor = formats.find(AttributedString.foregroundColours.contains(_))
    val backgroundColor = formats.find(AttributedString.backgroundColours.contains(_))
    val otherOptions = formats.filter(AttributedString.otherFormats.contains(_))
    Formats(foregroundColor, backgroundColor, otherOptions)
  }

  /**
    * Merges formats 1 with formats 2. Any conflicting format will result in format 2 overriding format 1
    * @param formats1
    * @param formats2
    */
  def merge(formats1: Formats, formats2: Formats) = {
    val fgCol = if (formats2.foregroundColor.isDefined) formats2.foregroundColor else formats1.foregroundColor
    val bgCol = if (formats2.backgroundColor.isDefined) formats2.backgroundColor else formats1.backgroundColor
    val rest = (formats1.otherFormats.toSet ++ formats2.otherFormats.toSet).toSeq
    Formats(fgCol, bgCol, rest)
  }

  protected [attributed_string] def nameForFormat(string: String) = string match {
    case RED => "RED"
    case BLUE => "BLUE"
    case BLACK => "BLACK"
    case CYAN => "CYAN"
    case YELLOW => "YELLOW"
    case GREEN => "GREEN"
    case MAGENTA => "MAGENTA"
    case WHITE => "WHITE"
    case RED_B => "RED_B"
    case BLUE_B => "BLUE_B"
    case BLACK_B => "BLACK_B"
    case CYAN_B => "CYAN_B"
    case YELLOW_B => "YELLOW_B"
    case GREEN_B => "GREEN_B"
    case MAGENTA_B => "MAGENTA_B"
    case WHITE_B => "WHITE_B"
    case BOLD => "BOLD"
    case UNDERLINED => "UNDERLINED"
    case BLINK => "BLINK"
    case REVERSED => "REVERSED"
    case INVISIBLE => "INVISIBLE"
  }

}
