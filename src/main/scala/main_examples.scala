import com.github.tototoshi.csv.*
import zio.*
import zio.Console.*

import scalemcsv.*
import scalemcsv.utils.logger

/** Example run with ZIO */
object RunApp extends ZIOAppDefault {

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
  logger.info(s"reading input csv file: $infile")
  implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
  reader.close()
  logger.info("done reading csv file")

  def run =
    for {
    result <- scalemcsv.suites.EnergySuite.applyWithZIO(dat, nFibers = 10)
        _ <- scalemcsv.utils.utils.writeResult2OutfileWithZIO(result, outfile = "/home/koenvandenberg/Downloads/scalemcsv_output.json")
    } yield ()
}

/** Example normal run */
def RunNormal(): Unit =

  // Reading in the CSV file
  val infile: String = "/home/koenvandenberg/insertdata/lisp/energy/benchmark/energy_data_4.csv"
  logger.info(s"reading input csv file: $infile")
  implicit object MyFormat extends DefaultCSVFormat:
    override val delimiter = '|'
  val reader = CSVReader.open(infile)
  val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
  reader.close()
  logger.info("done reading csv file")

  val result = scalemcsv.suites.EnergySuite.apply(dat)
  scalemcsv.utils.utils.writeResult2Outfile(result, outfile = "/home/koenvandenberg/Downloads/scalemcsv_output.json")


