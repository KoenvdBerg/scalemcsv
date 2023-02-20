package model

case class ValidationResult(
  indicesFound: IndexedSeq[Int],
  valuesFound: Vector[String],
  totalFound: Int,
  displayMessage: String,
  column: String,
  usedValidation: String
)

trait ColumnValidation:
  def logic(values: Vector[String]): Boolean
  def message: String
  def validationName: String
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