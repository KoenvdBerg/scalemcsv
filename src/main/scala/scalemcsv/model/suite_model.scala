package scalemcsv.model

import scala.concurrent.{ Future, ExecutionContext }
import ExecutionContext.Implicits.global

import scalemcsv.model.ValidationResult
import scalemcsv.validator.*
import scalemcsv.utils.logger

/**
 * Defines a specification of a validation in a validation suite
 * @param column The name of the column as in the csv header row
 * @param depends The name(s) of the column(s) that contain the data used for this validation
 * @param rowCondition Lambda expression that describes which part of the data should be validated and which not.
 *                     For examples, please consult the README file of scalemcsv.
 * @param validation The validation to be used for this validation. For available validations, please consult the
 *                   README of scalemcsv.
 */
case class SuiteSpec(
  column: String,
  depends: Vector[String],
  rowCondition: Vector[String] => Boolean = (x: Vector[String]) => x.head match {
    case _ => true
  },
  validation: ColumnValidation
)

/** Defines how a Validation looks like */
trait SuiteModel:

  /** The name of the validation suite */
  def suiteName: String

  /**
   * Colletion of the specifications in this validation suite
   */
  def suiteSpecs: List[SuiteSpec]

  /**
   * Applies the validation as defined in the SuiteSpec to the data
   * @param data The loaded in CSV data
   * @return A collection of the validation results
   */
  def apply(data: Map[String, Vector[String]]): List[ValidationResult] =
    val nValidations = this.suiteSpecs.length
    this.suiteSpecs.zipWithIndex.map((spec, index) =>
      val appliedValidation = spec.validation.validate(
        data = data,
        spec = spec)
      logger.info(s"finished ${index + 1} / ${nValidations}:  [${appliedValidation.totalFound} hits] for ${spec.validation.validationName} on ${appliedValidation.column}")
      appliedValidation
    )






// YARD:
// WITH FUTURE:
//def apply(data: Map[String, Vector[String]]): List[Future[ValidationResult]] =
//  this.suiteSpecs.map(spec =>
//    Future {
//      val relevantData = spec.depends.map(data(_))
//      spec.validation.validate(
//        values = relevantData,
//        column = spec.column,
//        rowCondition = relevantData.transpose.map(spec.rowCondition).toVector)
//    })
//    for {
//      i <- this.suiteSpecs
//    } yield
//      val relevantData = i.depends.map(data(_))
//      i.validation.validate(
//        values = relevantData,
//        column = i.column,
//        rowCondition = relevantData.transpose.map(i.rowCondition)
//    )

