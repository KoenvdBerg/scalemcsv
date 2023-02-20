package scalemcsv.model

object DataModel:

  def getLazyColumnVector(colname: String, data: List[Map[String, String]]): Vector[String] =
    lazy val colVector = (for i <- data yield i(colname)).toVector
    colVector

  def dataMap(CsvData: List[Map[String, String]]): Map[String, Vector[String]] =
    val headers = CsvData.head.keys
    val data = for {
      i <- headers
    } yield (i, this.getLazyColumnVector(i, CsvData))
    data.toMap