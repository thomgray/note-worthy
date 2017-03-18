package com.gray.note.util

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
  
  def trueLength(string: String) = unitalicise(string.replaceAll("\u001b\\[(\\d+)m", "")).length
  def removeFormatting(string: String) = string.replaceAll("\u001b\\[(\\d+)m", "")

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

  def italicise(string: String) = string
    .replaceAll("a", "𝘢")
    .replaceAll("b", "𝘣")
    .replaceAll("c", "𝘤")
    .replaceAll("d", "𝘥")
    .replaceAll("e", "𝘦")
    .replaceAll("f", "𝘧")
    .replaceAll("g", "𝘨")
    .replaceAll("h", "𝘩")
    .replaceAll("i", "𝘪")
    .replaceAll("j", "𝘫")
    .replaceAll("k", "𝘬")
    .replaceAll("l", "𝘭")
    .replaceAll("m", "𝘮")
    .replaceAll("n", "𝘯")
    .replaceAll("o", "𝘰")
    .replaceAll("p", "𝘱")
    .replaceAll("q", "𝘲")
    .replaceAll("r", "𝘳")
    .replaceAll("s", "𝘴")
    .replaceAll("t", "𝘵")
    .replaceAll("u", "𝘶")
    .replaceAll("v", "𝘷")
    .replaceAll("w", "𝘸")
    .replaceAll("x", "𝘹")
    .replaceAll("y", "𝘺")
    .replaceAll("z", "𝘻")
    .replaceAll("A", "𝘈")
    .replaceAll("B", "𝘉")
    .replaceAll("C", "𝘊")
    .replaceAll("D", "𝘋")
    .replaceAll("E", "𝘌")
    .replaceAll("F", "𝘍")
    .replaceAll("G", "𝘎")
    .replaceAll("H", "𝘏")
    .replaceAll("I", "𝘐")
    .replaceAll("J", "𝘑")
    .replaceAll("K", "𝘒")
    .replaceAll("L", "𝘓")
    .replaceAll("M", "𝘔")
    .replaceAll("N", "𝘕")
    .replaceAll("O", "𝘖")
    .replaceAll("P", "𝘗")
    .replaceAll("Q", "𝘘")
    .replaceAll("R", "𝘙")
    .replaceAll("S", "𝘚")
    .replaceAll("T", "𝘛")
    .replaceAll("U", "𝘜")
    .replaceAll("V", "𝘝")
    .replaceAll("W", "𝘞")
    .replaceAll("X", "𝘟")
    .replaceAll("Y", "𝘠")
    .replaceAll("Z", "𝘡")

  
  def unitalicise(string: String)= string
    .replaceAll("𝘢","a")
    .replaceAll("𝘣","b")
    .replaceAll("𝘤","c")
    .replaceAll("𝘥","d")
    .replaceAll("𝘦","e")
    .replaceAll("𝘧","f")
    .replaceAll("𝘨","g")
    .replaceAll("𝘩","h")
    .replaceAll("𝘪","i")
    .replaceAll("𝘫","j")
    .replaceAll("𝘬","k")
    .replaceAll("𝘭","l")
    .replaceAll("𝘮","m")
    .replaceAll("𝘯","n")
    .replaceAll("𝘰","o")
    .replaceAll("𝘱","p")
    .replaceAll("𝘲","q")
    .replaceAll("𝘳","r")
    .replaceAll("𝘴","s")
    .replaceAll("𝘵","t")
    .replaceAll("𝘶","u")
    .replaceAll("𝘷","v")
    .replaceAll("𝘸","w")
    .replaceAll("𝘹","x")
    .replaceAll("𝘺","y")
    .replaceAll("𝘻","z")
    .replaceAll("𝘈","A")
    .replaceAll("𝘉","B")
    .replaceAll("𝘊","C")
    .replaceAll("𝘋","D")
    .replaceAll("𝘌","E")
    .replaceAll("𝘍","F")
    .replaceAll("𝘎","G")
    .replaceAll("𝘏","H")
    .replaceAll("𝘐","I")
    .replaceAll("𝘑","J")
    .replaceAll("𝘒","K")
    .replaceAll("𝘓","L")
    .replaceAll("𝘔","M")
    .replaceAll("𝘕","N")
    .replaceAll("𝘖","O")
    .replaceAll("𝘗","P")
    .replaceAll("𝘘","Q")
    .replaceAll("𝘙","R")
    .replaceAll("𝘚","S")
    .replaceAll("𝘛","T")
    .replaceAll("𝘜","U")
    .replaceAll("𝘝","V")
    .replaceAll("𝘞","W")
    .replaceAll("𝘟","X")
    .replaceAll("𝘠","Y")
    .replaceAll("𝘡","Z")


}
