package validator

import jdk.jshell.spi.ExecutionControl.NotImplementedException

import scala.util.matching.Regex
import model.*

import java.text.SimpleDateFormat
import java.util.Date

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
  override def message: String =
    if inverse then s"The pattern: [${pattern}] was matched and should not be matched."
    else s"The pattern: [${pattern}] not was matched and should be matched."
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

class CheckDateNotInFuture(format: String) extends ColumnValidation:
  override def logic(values: Vector[String]): Boolean =
    try
      val parser = SimpleDateFormat(format)
      val x = parser.parse(values.head)
      x.before(new Date())
    catch
      case pe: java.text.ParseException => false

  override def message: String = s"Date value should be before today"

  override def validationName: String = "CheckDateNotInFuture"

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

class CheckNCharacters(maxNChars: Int) extends ColumnValidation:
  override def logic(values: Vector[String]): Boolean =
    values.head.length <= maxNChars

  override def message: String = s"Value should not exceed the character amount of $maxNChars"

  override def validationName: String = "CheckNCharacters"

class CheckInRange(rangeStart: Option[Float], rangeEnd: Option[Float]) extends ColumnValidation:
  override def logic(values: Vector[String]): Boolean =
    try
      val xf = values.head.toFloat
      (rangeStart, rangeEnd) match
        case (None, Some(rangeEnd)) => xf <= rangeEnd
        case (Some(rangeStart), None) => xf >= rangeStart
        case (Some(rangeStart), Some(rangeEnd)) => xf >= rangeStart & xf <= rangeEnd
        case (None, None) => throw new NotImplementedException("CheckInRange should at least have 1 value for either rangeStart or rangeEnd")
    catch
      // in case cannot be converted to float, value is not in range and thus wrong.
      case nfm: NumberFormatException => false


  override def message: String = (rangeStart, rangeEnd) match
    case (None, Some(rangeEnd)) => s"value should be <= $rangeEnd"
    case (Some(rangeStart), None) => s"value should be >= $rangeStart"
    case (Some(rangeStart), Some(rangeEnd)) => s"value should be: $rangeStart <= value <= $rangeEnd"

  override def validationName: String = "CheckInRange"
