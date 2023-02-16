/**
 * DONE: output the result to JSON
 * DONE: make object/class/function to load in the CSV including all columns to 1 data structure. Use getColumnVector(). make it callable like dat.column to obtain the vector
 * TODO: build ignore null by building it into logic as default to true. It can be added to trait as parameter
 * to make it avaiable to all instances. --> better still, make it ignore null as per default by just changing the logic
 * TODO: make more validations for dates, like seen in csv-validator
 * TODO: input config file as JSON
 * TODO: make multiple traits like: MapValidationRule, MultiColumnValidationRule
 * TODO: add the rowcondition to the validation by adding depends parameter to validate
 *  - Do this by changing the logic to work with >2 inputs and the validate to loop over the index of the values and
 *  have the other column as input as well. Then something like: res = for i <- idx yield logic(v1(i), v2(i))
 */

import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import validator.*
import utils.utils.*
import model.*

@main def run(): Unit =

  val t0 = System.currentTimeMillis()

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_0.csv"
    implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = DataModel.dataMap(reader.allWithHeaders())
  reader.close()

  // Performing data validations:
  val validationObject = List(
    CheckAllDigits.validate(dat("ID"), "ID"),
    CheckFloat.validate(dat("capacity"), "capacity"),
    CheckAllDigits.validate(dat("year"), "year"),
    CheckAllDigits.validate(Vector("2022", "2020", "lkds"), "year")
  )

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
 */

