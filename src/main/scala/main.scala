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
 * TODO: write in-code documentation
 * TODO: write README
 * DONE: add the rowcondition to the validation by adding depends parameter to validate
 *  - Do this by changing the logic to work with >2 inputs and the validate to loop over the index of the values and
 *  have the other column as input as well. Then something like: res = for i <- idx yield logic(v1(i), v2(i))
 */

import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import utils.utils.*
import utils.logger
import model.*
import suites.*
import java.io.*


@main def run(): Unit =

  logger.info("starting scalemcsv")

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
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




// YARD
//def readData(infile: String): Map[String, Vector[String]] =
//  implicit object MyFormat extends DefaultCSVFormat:
//    override val delimiter = '|'
//  val reader = CSVReader.open(infile)
//  val dat = DataModel.dataMap(reader.allWithHeaders())
//  reader.close()
//  dat
//import zio.*
//import zio.Console.*
//object MyApp extends ZIOAppDefault:
//  def run =
//    for {
////      t0 <- System.currentTimeMillis()
//      result <- EnergySuite.apply(readData("/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_0.csv"))
//      //  println(toJson(ValidationResult2Map(result)))
////      t1 <- System.currentTimeMillis()
////       _ <- println ("Elapsed time: " + (t1 - t0) / 1000f + " s")
//    } yield ZIO[Any, Nothing, List[ValidationResult]]()
//
//  val runtime = Runtime.default
//  Unsafe.unsafe { implicit unsafe =>
//    runtime.unsafe.run(ZIO.attempt(EnergySuite.apply(dat))).getOrThrowFiberFailure()
//}

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

