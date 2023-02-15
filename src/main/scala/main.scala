/**
 * DONE: output the result to JSON
 * TODO: input config file as JSON
 * TODO: make multiple traits like: MapValidationRule, MultiColumnValidationRule
 * TODO: add the rowcondition to the validation by adding depends parameter to validate
 */

import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import validator.*
import utils.utils.*

@main def run(): Unit =

  val t0 = System.currentTimeMillis()

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_0.csv"
    implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val allData = reader.allWithHeaders()

  // Performing data validations:
  val validationObject = jsonValidationResult(List(
    new CheckAllDigits().validate(getColumnVector("ID", allData), "ID"),
    new CheckFloat().validate(getColumnVector("capacity", allData), "capacity"),
    new CheckAllDigits().validate(getColumnVector("year", allData), "year")
  ))
  println(validationObject)

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
 */

