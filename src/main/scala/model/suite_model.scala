package model

import model.ValidationResult
import validator.*
import utils.logger
import scala.collection.parallel.CollectionConverters._

import scala.concurrent.{ Future, ExecutionContext }
import ExecutionContext.Implicits.global


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
      this.suiteSpecs.zipWithIndex.map((spec, index) =>
        val relevantData = spec.depends.map(data(_))
        logger.info(s"processing valdidation ${index+1} / ${this.suiteSpecs.length} named: ${spec.validation.validationName}")
        spec.validation.validate(
          columnValues = relevantData,
          column = spec.column,
          rowCondition = relevantData.transpose.map(spec.rowCondition).toVector))





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

