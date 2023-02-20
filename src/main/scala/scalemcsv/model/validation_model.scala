package scalemcsv.model

/**
 * Defines the result of a validation
 * @param indicesFound The indices of the hits where the validation didn't pass
 * @param valuesFound The values of the hits where the validation didn't pass
 * @param totalFound The total amount of hits found
 * @param displayMessage The message that accompanies each hit
 * @param column The column on which the validation was performed
 * @param usedValidation The validation that was used to generate this validation result
 */
case class ValidationResult(
  indicesFound: IndexedSeq[Int],
  valuesFound: Vector[String],
  totalFound: Int,
  displayMessage: String,
  column: String,
  usedValidation: String
)

trait ColumnValidation:

  /**
   * The logic behind the validation that returns either true or false.
   * @param values The incoming values consist of a vector that hold the values as they are mentioned in the 'depends'
   *               clause of a SuiteSpec. Example:
   *
   *               depends = Vector("column1", "column2") --> values = Vector("valuecol1", "valuecol2")
   * @return true or false based on logic
   */
  def logic(values: Vector[String]): Boolean

  /** The message to display when logic doesn't hold */
  def message: String

  /** Name of the validation */
  def validationName: String

  /**
   * Applies the defined logic to the CSV data. Returns CheckHeaderPresent validation if any of the columns in the
   * 'depends' clause of a SuiteSpec is not present in the data.
   * @param data The CSV data. Example:
   *             dat = Map(
   *                "column1" -> Vector("val1", "val2", "valN"),
   *                "column2" -> Vector("val1", "val2", "valN"),
   *                "columnN" -> Vector("val1", "val2", "valN"))
   * @param spec Instance of a SuiteSpec
   * @return A filled ValidationResult
   */
  def validate(data: Map[String, Vector[String]], spec: SuiteSpec): ValidationResult =
    try
      val relevantData = spec.depends.map(data(_)).transpose  // this can cause the error for the catch below
      val appliedRowCondition = relevantData.map(spec.rowCondition)
      val appliedLogic = relevantData.map(v => this.logic(v))
      val appliedTotal = Vector(appliedRowCondition, appliedLogic).transpose.map(a =>
        (a(0), a(1)) match
          case (true, true) => true
          case (true, false) => false
          case (false, _) => true)  // if rowcondition not met, then always true
      val foundIndices: Vector[Int] = appliedTotal.zipWithIndex.filter(_(0) == false).map(_(1))
      val foundValues = foundIndices.map(i => data(spec.column)(i))
      ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, spec.column, this.validationName)
    catch
      case nsee: NoSuchElementException =>
        ValidationResult(
          Vector(0),
          spec.depends, 1,
          s"the headers defined in the depends cannot be found in the csv data, which causes the following validation to not be executed: $spec",
          spec.column, "CheckHeaderPresent")