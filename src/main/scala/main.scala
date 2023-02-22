import scala.util.matching.Regex
import com.github.tototoshi.csv.*
import zio.*
import zio.Console.*

import scalemcsv.utils.utils.*
import scalemcsv.utils.logger
import scalemcsv.model.DataModel
import scalemcsv.suites.*

/** Example run with ZIO */
object RunApp extends ZIOAppDefault {

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
  logger.info(s"reading input csv file: $infile")
  implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = DataModel.dataMap(reader.allWithHeaders())
  reader.close()
  logger.info("done reading csv file")

  def run =
    for {
    result <- EnergySuite.applyWithZIO(dat, nFibers = 10)
      _ <- writeResult2OutfileWithZIO(result)
    } yield ()
}

/** Example normal run */
@main def run(): Unit =

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
  logger.info(s"reading input csv file: $infile")
  implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = DataModel.dataMap(reader.allWithHeaders())
  reader.close()
  logger.info("done reading csv file")

  val result = EnergySuite.apply(dat)
  writeResult2Outfile(result)


