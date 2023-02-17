package suites

import validator.*
import model.*


object EnergySuite extends SuiteModel:
  override def suiteName: String = "energy suite"
  override def suiteSpecs: List[SuiteSpec] =
    List(
      SuiteSpec(
        column = "reporting_date",
        depends = List("reporting_date"),
        rowCondition = ((vals: List[String]) => vals.head match {
          case "" => false
          case _ => true
        }),
        validation = new CheckDateFormat("yyyy-MM-dd")),
    )

//List(
//  CheckAllDigits.validate(
//    data("ID"),
//    "ID",
//    rowCondition = data("ID").map(_ => true)),
//  CheckFloat.validate(
//    data("capacity"),
//    "capacity",
//    rowCondition = data("capacity").map {
//      case "" => false
//      case _ => true
//    }),
//  CheckAllDigits.validate(
//    data("year"),
//    "year",
//    rowCondition = data("year").map(_ => true)),
//  new CheckNotPatternMatch(pattern = "link\\sunavailable".r).validate(
//    data("weblink"),
//    "weblink",
//    rowCondition = data("source").map {
//      case "REE" => false
//      case _ => true
//    }),
//  CheckNotNull.validate(
//    data("source_type"),
//    column = "source_type",
//    rowCondition = data("source_type").map(_ => true)
//  ),
//  CheckNotNull.validate(
//    data("source"),
//    column = "source",
//    rowCondition = data("source").map(_ => true)
//  ),
//  CheckNotNull.validate(
//    data("reporting_date"),
//    column = "reporting_date",
//    rowCondition = data("reporting_date").map(_ => true)
//  ),
//  new CheckDateFormat("yyyy-MM-dd").validate(
//    data("reporting_date"),
//    column = "reporting_date",
//    rowCondition = data("reporting_date").map {
//      case "" => false
//      case _ => true
//    }
//  ),
//  new CheckDateAGreaterThanDateB(format = "yyyy-MM-dd").validate(
//    List(
//      Vector("2022-01-01", "2000-11-11"),
//      Vector("2022-02-02", "1999-12-12")
//    ),
//    column = "testdate",
//    rowCondition = Vector(true, true)
//  )
//)