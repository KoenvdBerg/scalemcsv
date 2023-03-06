import com.github.tototoshi.csv.*
import mainargs.{Flag, ParserForMethods, arg, main}

import scalemcsv.*
import scalemcsv.utils.logger


object Main:
  // TODO: make sure that suite can be selected here as well,
  //  possibly by defining a main to investigate which suites are implemented.
  @main def validatexlsx(
    @arg(short = 'f', doc = "input xlsx file")
    infile: String,
    @arg(short = 'o', doc = "path to the output xlsx file")
    outfile: String): Unit =

    logger.info(s"reading input xlsx file: $infile")
    val dat = XlsxReader.readTable(xlsPath = infile)

    logger.info(s"validating ${dat(dat.keys.head).size} records")
    val result = scalemcsv.suites.OrderSuite.apply(dat)

    logger.info(s"writing output file: $outfile")
    XlsxWriter.writeToXlsx(outfile, result)

  @main def validatecsv(
    @arg(short = 'f', doc = "input csv file")
    infile: String,
    @arg(short = 'o', doc = "path to the output xlsx file")
    outfile: String,
    @arg(short = 'd', doc = "csv delimiter")
    delim: String): Unit =

      // Reading in the CSV file
      logger.info(s"reading input csv file: $infile")
      implicit object MyFormat extends DefaultCSVFormat:
        override val delimiter = delim.head
      val reader = CSVReader.open(infile)
      val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
      reader.close()

      logger.info(s"validating ${dat(dat.keys.head).size} records")
      val result = scalemcsv.suites.OrderSuite.apply(dat)

      logger.info(s"writing output file: $outfile")
      XlsxWriter.writeToXlsx(outfile, result)

  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args)
