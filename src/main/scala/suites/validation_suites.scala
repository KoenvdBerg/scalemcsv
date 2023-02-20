package suites

import validator.*
import model.*


object EnergySuite extends SuiteModel:
  override def suiteName: String = "energy suite"
  override def suiteSpecs: List[SuiteSpec] =
    List(
      SuiteSpec(
        column = "ID",
        depends = Vector("ID"),
        validation = CheckAllDigits
      ),
      SuiteSpec(
        column = "technology",
        depends = Vector("technology", "KLDSJG"),
        validation = new CheckNCharacters(50)
      ),
      SuiteSpec(
        column = "source",
        depends = Vector("source"),
        validation = CheckNotNull
      ),
      SuiteSpec(
        column = "source_type",
        depends = Vector("source_type"),
        validation = CheckNotNull
      ),
      SuiteSpec(
        column = "weblink",
        depends = Vector("weblink", "source"),
        rowCondition = (vals: Vector[String]) => vals(1) match {
          case "REE" => true
          case _ => false
        },
        validation = new CheckPatternMatch("link\\sunavailable".r, inverse = true)
      ),
      SuiteSpec(
        column = "year",
        depends = Vector("year"),
        validation = CheckAllDigits,
        rowCondition = (vals: Vector[String]) => if vals.head.length == 4 then true else false
      ),
      SuiteSpec(
        column = "country",
        depends = Vector("country"),
        validation = new CheckPatternMatch(
      "AL|AT|BA|BE|BG|CH|CY|CZ|DE|DK|EE|ES|FI|FR|GB|GE|GR|HR|HU|IE|IS|IT|LT|LU|LV|MD|ME|MK|MT|NI|NL|NO|PL|PT|RO|RS|SE|SI|SK|TR|UA|XK".r)),
      SuiteSpec(
        column = "capacity",
        depends = Vector("capacity"),
        rowCondition = (x: Vector[String]) => x.head match{case _ => true},
        validation = CheckFloat
      ),
      SuiteSpec(
        column = "capacity",
        depends = Vector("capacity"),
        validation = new CheckInRange(rangeStart = Some(0), rangeEnd = None)
      ),
      SuiteSpec(
        column = "capacity_definition",
        depends = Vector("capacity_definition"),
        validation = new CheckPatternMatch(pattern = "(Gross\\scapacity|Net\\scapacity|Unknown)".r)
      ),
      SuiteSpec(
        column = "energy_source_level_0",
        depends = Vector("energy_source_level_0"),
        validation = new CheckPatternMatch(pattern = "(True|False)".r)
      ),
      SuiteSpec(
        column = "energy_source_level_1",
        depends = Vector("energy_source_level_1"),
        validation = new CheckPatternMatch(pattern = "(True|False)".r)
      ),
      SuiteSpec(
        column = "energy_source_level_2",
        depends = Vector("energy_source_level_2"),
        validation = new CheckPatternMatch(pattern = "(True|False)".r)
      ),
      SuiteSpec(
        column = "energy_source_level_3",
        depends = Vector("energy_source_level_3"),
        validation = new CheckPatternMatch(pattern = "(True|False)".r)
      ),
      SuiteSpec(
        column = "technology_level",
        depends = Vector("technology_level"),
        validation = new CheckPatternMatch(pattern = "(True|False)".r)
      ),
      SuiteSpec(
        column = "reporting_date",
        depends = Vector("reporting_date"),
        validation = new CheckPatternMatch(pattern = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$".r)
      ),
      SuiteSpec(
        column = "reporting_date",
        depends = Vector("reporting_date"),
        validation = new CheckDateNotInFuture(format = "yyyy-MM-dd")
      ),
      SuiteSpec(
        column = "reporting_date",
        depends = Vector("reporting_date"),
        validation = CheckNotNull
      ),
    )

//def applySuite(data: Map[String, Vector[String]]): List[ValidationResult] =
//  List(
//    CheckAllDigits.validate(
//      data("ID"),
//      "ID",
//      rowCondition = data("ID").map(_ => true)),
//    CheckFloat.validate(
//      data("capacity"),
//      "capacity",
//      rowCondition = data("capacity").map {
//        case "" => false
//        case _ => true
//      }),
//    CheckAllDigits.validate(
//      data("year"),
//      "year",
//      rowCondition = data("year").map(_ => true)),
//    new CheckNotPatternMatch(pattern = "link\\sunavailable".r).validate(
//      data("weblink"),
//      "weblink",
//      rowCondition = data("source").map {
//        case "REE" => false
//        case _ => true
//      }),
//    CheckNotNull.validate(
//      data("source_type"),
//      column = "source_type",
//      rowCondition = data("source_type").map(_ => true)
//    ),
//    CheckNotNull.validate(
//      data("source"),
//      column = "source",
//      rowCondition = data("source").map(_ => true)
//    ),
//    CheckNotNull.validate(
//      data("reporting_date"),
//      column = "reporting_date",
//      rowCondition = data("reporting_date").map(_ => true)
//    ),
//    new CheckDateFormat("yyyy-MM-dd").validate(
//      data("reporting_date"),
//      column = "reporting_date",
//      rowCondition = data("reporting_date").map {
//        case "" => false
//        case _ => true
//      }
//    ),
//    new CheckDateAGreaterThanDateB(format = "yyyy-MM-dd").validate(
//      List(
//        Vector("2022-01-01", "2000-11-11"),
//        Vector("2022-02-02", "1999-12-12")
//      ),
//      column = "testdate",
//      rowCondition = Vector(true, true)
//    )
//  )