package validator

import scala.util.matching.Regex
import model.*
import java.text.SimpleDateFormat

object CheckAllDigits extends ColumnValidation:
  override def logic(x: Vector[String]): Boolean =
    val numberPattern: Regex = "^[0-9]+$".r
    x.head match
      case numberPattern() => true
      case _ => false

  override def message: String = "The value should be an Integer"

  override def validationName: String = "CheckIsInt"


object CheckFloat extends ColumnValidation:
  override def logic(x: Vector[String]): Boolean =
    try
      val converted = x.head.toFloat
      true
    catch
      case nfm: NumberFormatException => false

  override def message: String = "The value should be a Float"

  override def validationName: String = "CheckFloat"

class CheckPatternMatch(pattern: Regex, inverse: Boolean = false) extends ColumnValidation:
  override def logic(x: Vector[String]): Boolean =
    if inverse then !pattern.matches(x.head) else pattern.matches(x.head)
  override def message: String = s"The pattern: [${pattern}] was not matched."
  override def validationName: String = "CheckPatternMatch"

object CheckNotNull extends ColumnValidation:
  override def logic(x: Vector[String]): Boolean =
    x.head.toLowerCase() match
      case "" => false
      case "null" => false
      case "na" => false
      case "nan" => false
      case _ => true
  override def message: String = "Value should not be null"
  override def validationName: String = "CheckNotNull"


class CheckDateAGreaterThanDateB(format: String) extends ColumnValidation:
  override def logic(values: Vector[String]): Boolean =
    try
      val parser = SimpleDateFormat(format)
      val a = parser.parse(values(0))
      val b = parser.parse(values(1))
      a.before(b)
    catch
      case pe: java.text.ParseException => false

  override def message: String = s"Date A should be greater than Date B in format:$format"

  override def validationName: String = "CheckDateAGreaterThanDate"

class CheckDateFormat(format: String) extends ColumnValidation:

  override def logic(values: Vector[String]): Boolean =
    try
      val parser = SimpleDateFormat(format)
      parser.parse(values.head)
      true
    catch
      case pe: java.text.ParseException => false

  override def message: String = s"Value should be in format $format"

  override def validationName: String = "CheckDateFormat"
