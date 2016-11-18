package com.gray.util

import com.gray.note.Config

import scala.io.AnsiColor

trait Formatting extends AnsiColor{

  def trimEmptyLines(string: String) = {
    val prefix = """^(\s*(\n|\r))*""".r.findFirstIn(string).getOrElse("")
    val suffix = """\s*$""".r.findFirstIn(string).getOrElse("")
    string.stripPrefix(prefix).stripSuffix(suffix)
  }

  def indentString(string: String, indent: String = Config.standardTab) = {
    string.split("(\\n|\\r)").map(indent + _).mkString("\n")
  }

  def concatenate(string: String, times: Int) = (for (_ <- 0 until times) yield string).mkString

  def splitLines(string: String) = string.split("(\\r|\\n)")
  
  def trueLength(string: String) = string.replaceAll("\u001b\\[(\\d+)m", "").length

  def ansiColours = List(RED, BLUE, WHITE, BLACK, GREEN, YELLOW, MAGENTA, CYAN, RED_B, BLUE_B, WHITE_B, BLACK_B, GREEN_B, YELLOW_B, MAGENTA_B, CYAN_B, RESET, REVERSED, BLINK, UNDERLINED, BOLD)

  def superscript(int: Int): String = superscript(int.toString)
  def superscript(int: String) = {
    int.replaceAll("0", "⁰")
    .replaceAll("1", "¹")
    .replaceAll("2", "²")
    .replaceAll("3", "³")
    .replaceAll("4", "⁴")
    .replaceAll("5", "⁵")
    .replaceAll("6", "⁶")
    .replaceAll("7", "⁷")
    .replaceAll("8", "⁸")
    .replaceAll("9", "⁹")
  }

  def subscript(int: Int): String = subscript(int.toString)
  def subscript(int: String) = {
    int.replaceAll("0", "₀")
    .replaceAll("1", "₁")
    .replaceAll("2", "₂")
    .replaceAll("3", "₃")
    .replaceAll("4", "₄")
    .replaceAll("5", "₅")
    .replaceAll("6", "₆")
    .replaceAll("7", "₇")
    .replaceAll("8", "₈")
    .replaceAll("9", "₉")
  }

}
