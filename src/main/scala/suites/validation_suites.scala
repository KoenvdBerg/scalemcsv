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
