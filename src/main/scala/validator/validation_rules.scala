package validator

import scala.util.matching.Regex
import model.*

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

  override def message: String = "The weblink cannot be unavailable if the source is BFE"

  override def validationName: String = "WebLinkNotAvailable"

//  override def logic(values: List[String]): Boolean =
//    values(1) match
//      case "BFE" => values(0) match
//        case "link unavailable" => false
//        case _ => true
//      case _ => true