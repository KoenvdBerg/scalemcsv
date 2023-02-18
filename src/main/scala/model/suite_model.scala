package model

import model.ValidationResult
import validator.*
import scala.collection.parallel.CollectionConverters._

case class SuiteSpec(
  column: String,
  depends: Vector[String],
  rowCondition: Vector[String] => Boolean,
  validation: ColumnValidation
)

trait SuiteModel:
  def suiteName: String
  def suiteSpecs: List[SuiteSpec]
  def apply(data: Map[String, Vector[String]]): List[ValidationResult] =
//    this.suiteSpecs.map(spec => spec.validation.validate(
//      values = spec.depends.map(data(_)),
//      column = spec.column,
//      rowCondition = spec.depends.map(data(_)).transpose.map(spec.rowCondition).toVector
//    ))


    for {
      i <- this.suiteSpecs
    } yield
      val relevantData = i.depends.map(data(_))
      i.validation.validate(
        values = relevantData,
        column = i.column,
        rowCondition = relevantData.transpose.map(i.rowCondition)
    )

