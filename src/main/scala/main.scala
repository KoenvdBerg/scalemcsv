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
  val pw = new PrintWriter(new File("/home/koenvandenberg/Downloads/scalemcsv_output.json"))
  pw.write(toJson(ValidationResult2Map(result)).replace("\\", "\\\\"))
  pw.close()

