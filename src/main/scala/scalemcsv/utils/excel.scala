/**
 * https://gist.github.com/satendrakumar/0214ba32e0e12ae548232b8566418cd1
 */


import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.{File, FileOutputStream}
import scala.util.control.NonFatal
import scala.jdk.CollectionConverters.*


object XlsxReader  {

  private val df = new DataFormatter(true)

  private def removeLineBreakingChars(cell: String): String = cell.replaceAll("[\\t\\n\\r]", " ")

  def rowToVector(row: Row, formulaEvaluator: FormulaEvaluator): Vector[String] =
    row.asScala.map(cell => removeLineBreakingChars(df.formatCellValue(cell, formulaEvaluator))).toVector

  def getAllRecords(sheet: Sheet, formulaEvaluator: FormulaEvaluator): Vector[Vector[String]] =
    sheet.asScala.map(row => rowToVector(row, formulaEvaluator)).toVector

  def readTable(xlsPath: String, headerIndex: Int = 0, sheetIndex: Int = 1): Map[String, Vector[String]] =
    // Reading in the sheet
    val file = new File(xlsPath)
    val workbook = WorkbookFactory.create(file)
    val formulaEvaluator: FormulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator()
    val sheet = workbook.getSheetAt(sheetIndex)

    // Reading in header row
    val headerRow = rowToVector(sheet.getRow(headerIndex), formulaEvaluator)

    // Reading in all the records
    val records = getAllRecords(sheet, formulaEvaluator)

    // Filtering out header row and empty records
    val filteredRecords = records.filter(r => r.size == headerRow.size & !r.sameElements(headerRow))

    // Making Map data structure
    val tmp = filteredRecords.transpose  // transposing to get the columns
    headerRow.zip(tmp).toMap
}

object XlsxWriter:

  def writeToXlsx(outfile: String, data: List[scalemcsv.model.ValidationResult]): Unit =
    val workbook = new XSSFWorkbook()
    val output = new FileOutputStream(outfile)
    val sheet = workbook.createSheet()

    val vectorData = data.flatMap(res => res.indicesFound.map(i =>
      Vector(res.column, res.totalFound, res.indicesFound(i), res.valuesFound(i), res.displayMessage, res.usedValidation)))

    // Write header:
    val headerData = Vector("column", "total_hits", "hit_index", "hit_value", "message", "used_validation")
    writeRow(0, sheet, headerData)

    // Write records:
    for
      index <- vectorData.indices
    do
      writeRow(index+1, sheet, vectorData(index))

    workbook.write(output)

  private def writeRow(rowIndex: Int, sheet: Sheet, data: Vector[Any]): Unit =
    val row = sheet.createRow(rowIndex)
    for
      c <- data.indices
    do
      val cell = row.createCell(c)
      data(c) match
        case value: String => cell.setCellValue(value)
        case value: Int => cell.setCellValue(value)
        case value: Float => cell.setCellValue(value)
