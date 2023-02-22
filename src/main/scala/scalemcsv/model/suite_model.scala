package scalemcsv.model

import zio.*
import zio.ZIOAspect.parallel

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
   * Collection of the specifications in this validation suite
   */
  def suiteSpecs: List[SuiteSpec]

  /**
   * Applies the validation as defined in the SuiteSpec to the data using ZIO
   * @param data The loaded in CSV data
   * @return A collection of the validation results
   */
  def applyWithZIO(data: Map[String, Vector[String]], nFibers: Int): Task[List[ValidationResult]] =
    for {
      result <- ZIO.foreachPar(this.suiteSpecs) {
        spec =>
          spec.validation.validateWithZIO(
            data = data,
            spec = spec)
      } @@ parallel(nFibers)
    } yield (result)

  /**
   * Applies the validation as defined in the SuiteSpec to the data
   *
   * @param data The loaded in CSV data
   * @return A collection of the validation results
   */
  def apply(data: Map[String, Vector[String]]): List[ValidationResult] =
    this.suiteSpecs.map(spec =>
      spec.validation.validate(
        data = data,
        spec = spec)
    )