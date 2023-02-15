package validator

import scala.util.matching.Regex
import model.*

class CheckAllDigits extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    val numberPattern: Regex = "^[0-9]+$".r
    x match
      case numberPattern() => true
      case _ => false

  override def message: String = "The value should be an Integer"

  override def validationName: String = "CheckIsInt"


class CheckFloat extends SingleColumnValidation:
  override def logic(x: String): Boolean =
    try
      val converted = x.toFloat
      true
    catch
      case nfm: NumberFormatException => false

  override def message: String = "The value should be a Float"

  override def validationName: String = "CheckFloat"