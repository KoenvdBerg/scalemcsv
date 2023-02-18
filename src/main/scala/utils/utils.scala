package utils

import model.ValidationResult
import com.typesafe.scalalogging.*

val logger = Logger("scalemcsv")

object utils:


  def printValidationResult(valres: ValidationResult): Unit =
    for {
      i <- valres.indicesFound.indices
    } do
      println(s"=== RESULT $i ===")
      println(s"index: ${valres.indicesFound(i)}\nvalue: ${valres.valuesFound(i)}\nmesssage: ${valres.displayMessage}\ncolumn: ${valres.column}")
      println(s"=================\n")

  def toJson(query: Any): String = query match
    case m: Map[String, Any] => s"{${m.map(toJson(_)).mkString(",")}}"
    case t: (String, Any) => s""""${t._1}":${toJson(t._2)}"""
    case ss: Seq[Any] => s"""[${ss.map(toJson(_)).mkString(",")}]"""
    case s: String => s""""$s""""
    case null => "null"
    case _ => query.toString

  def ValidationResult2Map(valresults: List[ValidationResult]): List[Map[String, Any]] =
        for {
          res <- valresults
        } yield Map(
          "used_validation" -> res.usedValidation,
          "column" -> res.column,
          "message" -> res.displayMessage,
          "total_errors" -> res.totalFound,
          "found_indices" -> res.indicesFound,
          "found_values" -> res.valuesFound)

