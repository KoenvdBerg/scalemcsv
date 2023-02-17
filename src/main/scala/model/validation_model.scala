package model

case class ValidationResult(
  indicesFound: IndexedSeq[Int],
  valuesFound: IndexedSeq[String],
  totalFound: Int,
  displayMessage: String,
  column: String,
  usedValidation: String
)

trait SingleColumnValidation:
  def logic(x: String): Boolean
  def message: String
  def validationName: String
  def validate(values: Vector[String], column: String, rowCondition: Vector[Boolean]): ValidationResult =
    val appliedLogic = values.map(v => this.logic(v))
    val res = for {
      i <- values.indices
    } yield if rowCondition(i) then appliedLogic(i) else true
    val foundIndices = for {
      i <- res.indices
      if !res(i)} yield i
    val foundValues = for {
      i <- res.indices
      if !res(i)
    } yield values(i)
    ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, column, this.validationName)

trait MultiColumnValidation:
  def logic(values: List[String]): Boolean
  def message: String
  def validationName: String
  def validate(values: List[Vector[String]], column: String, rowCondition: Vector[Boolean]): ValidationResult =
    val appliedLogic = values.transpose.map(v => this.logic(v))
    val res = for {
      i <- values.indices
    } yield if rowCondition(i) then appliedLogic(i) else true
    val foundIndices = for {
      i <- res.indices
      if !res(i)} yield i
    val foundValues = for {
      i <- res.indices
      if !res(i)
    } yield values(0)(i)
    ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, column, this.validationName)