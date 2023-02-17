package model

import model.ValidationResult
import validator.*

case class SuiteSpec(
  column: String,
  depends: List[String],
  rowCondition: List[String] => Boolean,
  validation: ColumnValidation
)

trait SuiteModel:
  def suiteName: String
  def suiteSpecs: List[SuiteSpec]
  def apply(data: Map[String, Vector[String]]): List[ValidationResult] =
    for {
      i <- this.suiteSpecs
    } yield i.validation.validate(
      values = i.depends.map(data(_)),
      column = i.column,
      rowCondition = i.depends.map(data(_)).transpose.map(i.rowCondition).toVector
    )

