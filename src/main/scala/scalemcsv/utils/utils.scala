package scalemcsv.utils

import scalemcsv.model.ValidationResult
import com.typesafe.scalalogging.*
import zio.*
import java.io.*

/** Logger used in scalemcsv */
val logger = Logger("scalemcsv")

object utils:

  /**
   * Converts a Map to a JSON string
   * @param query A filled Map() collection
   * @return JSON string
   */
  def toJson(query: Any): String = query match
    case m: Map[String, Any] => s"{${m.map(toJson(_)).mkString(",")}}"
    case t: (String, Any) => s""""${t._1}":${toJson(t._2)}"""
    case ss: Seq[Any] => s"""[${ss.map(toJson(_)).mkString(",")}]"""
    case s: String => s""""$s""""
    case null => "null"
    case _ => query.toString

  /**
   * Transforms the list of validation results to a Map() collection
   * @param valresults List of ValidationResults
   * @return Map as defined in this function.
   */
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

  /**
   * Writes a list of validation results to JSON in an outfile with ZIO
   * @param result List of validation results
   * @return Unit
   */
  def writeResult2OutfileWithZIO(result: List[ValidationResult]): Task[Unit] =
    val pw = new PrintWriter(new File("/home/koenvandenberg/Downloads/scalemcsv_output.json"))
    pw.write(toJson(ValidationResult2Map(result)).replace("\\", "\\\\"))
    pw.close()
    ZIO.succeed(())

  /**
   * Writes a list of validation results to JSON in an outfile
   *
   * @param result List of validation results
   * @return Unit
   */
  def writeResult2Outfile(result: List[ValidationResult]): Unit =
    val pw = new PrintWriter(new File("/home/koenvandenberg/Downloads/scalemcsv_output.json"))
    pw.write(toJson(ValidationResult2Map(result)).replace("\\", "\\\\"))
    pw.close()