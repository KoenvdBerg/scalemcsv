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
  def validate(values: Vector[String], column: String): ValidationResult =
    val res = values.map(v => this.logic(v))
    val foundIndices = for {
      i <- res.indices
      if !res(i)} yield i
    val foundValues = for {
      i <- res.indices
      if !res(i)
    } yield values(i)
    ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, column, this.validationName)
