package model

case class ValidationResult(
  indicesFound: IndexedSeq[Int],
  valuesFound: IndexedSeq[String],
  totalFound: Int,
  displayMessage: String,
  column: String,
  usedValidation: String
)

trait ColumnValidation:
  def logic(values: Vector[String]): Boolean
  def message: String
  def validationName: String
  def validate(columnValues: Vector[Vector[String]], column: String, rowCondition: Vector[Boolean]): ValidationResult =
    val appliedLogic = columnValues.transpose.map(v => this.logic(v))
    val res = for {
      i <- columnValues.head.indices
    } yield if rowCondition(i) then appliedLogic(i) else true
    val foundIndices = for {
      i <- res.indices
      if !res(i)} yield i
    val foundValues = for {
      i <- res.indices
      if !res(i)
    } yield columnValues.head(i)
    ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, column, this.validationName)