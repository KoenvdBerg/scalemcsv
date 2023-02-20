/**
 * DONE: output the result to JSON
 * DONE: make object/class/function to load in the CSV including all columns to 1 data structure. Use getColumnVector(). make it callable like dat.column to obtain the vector
 * DONE: build ignore null by building it into logic as default to true. It can be added to trait as parameter
 * to make it avaiable to all instances. --> better still, make it ignore null as per default by just changing the logic
 * DONE: make multiple traits like: MapValidationRule, MultiColumnValidationRule
 * DONE: make more validations for dates, like seen in csv-validator
 * DONE: make date A > date B validation using MulticolumnValidationRule
 * DONE: input config file as JSON
 *  like: val x = Map("rowCondition" -> ((x: Int) => x + 1))
 * DONE: make trait and model for suite
 * DONE: rewrite singlecolvaldiations to columnvalidations
 * DONE: add all the specs in validation_suites.scala
 * DONE: include header validations (filter for working specs in suiteSpecs)
 * DONE: remakce csv-validator energysuite
 * DONE: write in-code documentation and remove Yards
 * TODO: figure out how to make a SCALA library
 * TODO: write README
 * TODO: write unit tests
 * DONE: add the rowcondition to the validation by adding depends parameter to validate
 *  - Do this by changing the logic to work with >2 inputs and the validate to loop over the index of the values and
 *  have the other column as input as well. Then something like: res = for i <- idx yield logic(v1(i), v2(i))
 */

import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import java.io.*

import scalemcsv.utils.utils.*
import scalemcsv.utils.logger
import scalemcsv.model.*
import scalemcsv.suites.*

@main def run(): Unit =

  logger.info("starting scalemcsv")

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_0.csv"
  logger.info(s"reading input csv file: $infile")
    implicit object MyFormat extends DefaultCSVFormat:
      override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = DataModel.dataMap(reader.allWithHeaders())
  reader.close()
  logger.info("done reading csv file")

  // Performing data validations:
  logger.info("starting data validation")
  val result = EnergySuite.apply(dat)
  logger.info("finished data validation")

  // Writing result to outfile
//  val pw = new PrintWriter(new File("/tmp/scalemcsv_output.json"))
  val pw = new PrintWriter(new File("/home/koenvandenberg/Downloads/scalemcsv_output.json"))
  pw.write(toJson(ValidationResult2Map(result)).replace("\\", "\\\\"))
  pw.close()

