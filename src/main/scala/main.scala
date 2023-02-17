/**
 * DONE: output the result to JSON
 * DONE: make object/class/function to load in the CSV including all columns to 1 data structure. Use getColumnVector(). make it callable like dat.column to obtain the vector
 * DONE: build ignore null by building it into logic as default to true. It can be added to trait as parameter
 * to make it avaiable to all instances. --> better still, make it ignore null as per default by just changing the logic
 * TODO: make multiple traits like: MapValidationRule, MultiColumnValidationRule
 * DONE: make more validations for dates, like seen in csv-validator
 * TODO: input config file as JSON
 * TODO: add the rowcondition to the validation by adding depends parameter to validate
 *  - Do this by changing the logic to work with >2 inputs and the validate to loop over the index of the values and
 *  have the other column as input as well. Then something like: res = for i <- idx yield logic(v1(i), v2(i))
 */

import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import validator.*
import utils.utils.*
import model.*

import java.time.Duration
import scala.concurrent.Future
import concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


@main def run(): Unit =

  val t0 = System.currentTimeMillis()

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
    implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = DataModel.dataMap(reader.allWithHeaders())
  reader.close()

  // Performing data validations:
  val validationObject = List(
    CheckAllDigits.validate(
      dat("ID"),
      "ID",
      rowCondition = dat("ID").map(_ => true)),
    CheckFloat.validate(
      dat("capacity"),
      "capacity",
      rowCondition = dat("capacity").map {
        case "" => false
        case _ => true
      }),
    CheckAllDigits.validate(
      dat("year"),
      "year",
      rowCondition = dat("year").map(_ => true)),
    new CheckNotPatternMatch(pattern = "link\\sunavailable".r).validate(
      dat("weblink"),
      "weblink",
      rowCondition = dat("source").map {
        case "REE" => false
        case _ => true
      }),
    CheckNotNull.validate(
      dat("source_type"),
      column = "source_type",
      rowCondition = dat("source_type").map(_ => true)
    ),
    CheckNotNull.validate(
      dat("source"),
      column = "source",
      rowCondition = dat("source").map(_ => true)
    ),
    CheckNotNull.validate(
      dat("reporting_date"),
      column = "reporting_date",
      rowCondition = dat("reporting_date").map(_ => true)
    ))

  println(toJson(ValidationResult2Map(validationObject)))

  val t1 = System.currentTimeMillis()
  println("Elapsed time: " + (t1 - t0) + " ms")








/**
 * YARD:
 * trait EnergyData:
 * def ID: Vector[String]
 * def technology: Vector[String]
 *
 * class EnergyVal(file: String, delim: Char) extends EnergyData:
 *
 * implicit object MyFormat extends DefaultCSVFormat:
 * override val delimiter = delim
 *
 *
 * val reader = CSVReader.open(file)
 * val allData = reader.allWithHeaders()
 *
 * override def ID: Vector[String] =
 * (for i <- allData yield i("ID")).toVector
 *
 * override def technology: Vector[String] =
 * (for i <- allData yield i("technology")).toVector
 *
 * override def validate(values: Vector[String], column: String): ValidationResult =
 * val res = values.map(v => this.logic(v))
 * val foundIndices = for {
 * i <- res.indices
 * if !res(i) } yield i
 *
 * val foundValues = for {
 * i <- res.indices
 * if !res(i)
 * } yield values(i)
 *
 * ValidationResult(foundIndices, foundValues, foundIndices.length, this.message, column)
 *
 *
 *
 * val fres = Future(
 * new CheckDateFormat("yyyy-MM-dd").validate(
 * dat("reporting_date"),
 * column = "reporting_date",
 * rowCondition = dat("capacity").map {
 * case "" => false
 * case _ => true
 * }))
 *
 * val fres2 = Future(
 * new CheckNotPatternMatch(pattern = "link\\sunavailable".r).validate(
 * dat("weblink"),
 * "weblink",
 * rowCondition = dat("source").map {
 * case "REE" => false
 * case _ => true
 * }))
 *
 * fres.onComplete {
 * case Success(x) => println(toJson(ValidationResult2Map(List(x))))
 * //    case Failure(e) => e.printStackTrace
 * }
 */

