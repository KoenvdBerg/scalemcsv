package validator

import scala.util.matching.Regex
import model.*
import java.text.SimpleDateFormat

object CheckAllDigits extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    val numberPattern: Regex = "^[0-9]+$".r
    x match
      case numberPattern() => true
      case _ => false

  override def message: String = "The value should be an Integer"

  override def validationName: String = "CheckIsInt"


object CheckFloat extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    try
      val converted = x.toFloat
      true
    catch
      case nfm: NumberFormatException => false

  override def message: String = "The value should be a Float"

  override def validationName: String = "CheckFloat"

class CheckNotPatternMatch(pattern: Regex) extends SingleColumnValidation:

  private val patterToMatch: Regex = pattern
  override def logic(x: String): Boolean =
    x match
      case this.patterToMatch() => false
      case _ => true

  override def message: String = s"The pattern: \"${patterToMatch}\" was not matched."

  override def validationName: String = "WebLinkNotAvailable"

object CheckNotNull extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    x.toLowerCase() match
      case "" => false
      case "null" => false
      case "na" => false
      case "nan" => false
      case _ => true
  override def message: String = "Value should not be null"
  override def validationName: String = "WebLinkNotAvailable"


class CheckDateFormat(format: String) extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    try
      val parser = SimpleDateFormat(format)
      parser.parse(x)
      true
    catch
      case pe: java.text.ParseException => false

  override def message: String = s"Value should be in format $format"

  override def validationName: String = "CheckDateFormat"