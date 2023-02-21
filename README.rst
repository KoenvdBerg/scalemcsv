scalemcsv
============

This project outlines an attempt to define a way of thinking about
validating data in tabular format (CSV) inspired from its Python
homologue Great Expectations. It works by defining a set of
validations that can be repeatedly applied to a tabular dataset in CSV
format. This is especially useful in a data-pipeline scenario where
similar tabular (CSV) data is collected from different sources, and
you like to validate the data before it enters downstream
processing. The validations are stored in a validation suite that is
written entirely in Scala syntax.

**Features**

- **Extensible**: validations are written in Scala 3 and stored in
  validation suites. New validations can be easily added.
- **No-nonsense output**: output is a JSON file that contains location
  of values that did not pass the validation, including the erronuous
  value.
- **Quite performant**: single core validation speed is ~10 MB/s.

.. contents:: **Table of Contents**


Installation
-----------

To install and use scalemcsv, please do the following steps:

1. Clone this git-repository to a local folder with::

     ~$ git clone git@github.com:KoenvdBerg/csv-validator.git

2. Publish the project to your local library folder::

     ~$ cd scalemcsv
     ~$ sbt publishLocal

3. Add the following line to your ``build.sbt`` file to use scalemcsv
   in a new project::

     libraryDependencies += "com.github.scalemcsv" % "scalemcsv" % "0.1.0-SNAPSHOT"

   Make sure to reload IntelliJ sbt so that IntelliJ can load the library.

4. Add this to your own code to work with scalemcsv::

     import scalemcsv.*


Get started
-----------

Validating a CSV file with scalemcsv consists of the following three
steps that will be explained further below:

1. Loading in the CSV data using a CSV loading library.
2. Performing the validations using a predefined validation suite
3. Investigating the results

Let's go through each step with accompanying code examples. We'll be
using the ``energy_sample.csv`` data that is present in this
code-repository.

**1. Loading in the CSV data using a CSV loading library**

The library that we'll be using to load in the CSV can be found here:
https://github.com/tototoshi/scala-csv.

CSV data can be loaded in using the following code::

  import scalemcsv.*
  import com.github.tototoshi.csv.*

  @main def main(): Unit =
    // Reading in the CSV file
    val infile: String = "/path/to/energy_sample.csv"

    // declaring the delimiter
    implicit object MyFormat extends DefaultCSVFormat:
      override val delimiter = '|'
    val reader = CSVReader.open(infile)
    val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
    reader.close()

We use the ``scalemcsv.model.DataModel`` object and the ``dataMap``
method to read in the csv as: ``Map[String, Vector[String]]``. This
data object looks like follows (example)::

  val dat = Map(
    "column1" -> Vector("value1", "value2", "valueN")
    "column2" -> Vector("value1", "value2", "valueN")
    "columnN" -> Vector("value1", "value2", "valueN")
  )

**2. Performing the validations using a predefined validation suite**

To perform data-validations, include the following code in the main
function that was defined in the previous step::

  val result = scalemcsv.suites.EnergySuite.apply(dat)

This validates the data using the EnergySuite, that was included in
the scalemcsv repository as an example and has 18 validations that are
applied to the energy dataset.

It gives the following logs output to the run console::

  15:05:37.423 [main] INFO scalemcsv - finished 1 / 18:  [2 hits] for CheckIsInt on ID
  15:05:37.425 [main] INFO scalemcsv - finished 2 / 18:  [1 hits] for CheckNCharacters on technology
  15:05:37.426 [main] INFO scalemcsv - finished 3 / 18:  [0 hits] for CheckNotNull on source
  15:05:37.426 [main] INFO scalemcsv - finished 4 / 18:  [0 hits] for CheckNotNull on source_type
  15:05:37.426 [main] INFO scalemcsv - finished 5 / 18:  [1 hits] for CheckPatternMatch on weblink
  15:05:37.426 [main] INFO scalemcsv - finished 6 / 18:  [0 hits] for CheckIsInt on year
  15:05:37.427 [main] INFO scalemcsv - finished 7 / 18:  [0 hits] for CheckPatternMatch on country
  15:05:37.428 [main] INFO scalemcsv - finished 8 / 18:  [2 hits] for CheckFloat on capacity
  15:05:37.428 [main] INFO scalemcsv - finished 9 / 18:  [2 hits] for CheckInRange on capacity
  15:05:37.428 [main] INFO scalemcsv - finished 10 / 18:  [0 hits] for CheckPatternMatch on capacity_definition
  15:05:37.428 [main] INFO scalemcsv - finished 11 / 18:  [0 hits] for CheckPatternMatch on energy_source_level_0
  15:05:37.428 [main] INFO scalemcsv - finished 12 / 18:  [0 hits] for CheckPatternMatch on energy_source_level_1
  15:05:37.428 [main] INFO scalemcsv - finished 13 / 18:  [0 hits] for CheckPatternMatch on energy_source_level_2
  15:05:37.429 [main] INFO scalemcsv - finished 14 / 18:  [0 hits] for CheckPatternMatch on energy_source_level_3
  15:05:37.429 [main] INFO scalemcsv - finished 15 / 18:  [0 hits] for CheckPatternMatch on technology_level
  15:05:37.429 [main] INFO scalemcsv - finished 16 / 18:  [1 hits] for CheckPatternMatch on reporting_date
  15:05:37.430 [main] INFO scalemcsv - finished 17 / 18:  [1 hits] for CheckDateNotInFuture on reporting_date
  15:05:37.431 [main] INFO scalemcsv - finished 18 / 18:  [0 hits] for CheckNotNull on reporting_date
  
  Process finished with exit code 0

The output to the console shows how many hits were found for each
validation that was defined in the ``EnergySuite`` validation suite.


**3. Investigating the results**

To investigate the results, let's write the result to JSON first. Add
the following code to the bottom of your main function::

  import scalemcsv.utils.utils.{ValidationResult2Map, toJson}
  import java.io.{File, PrintWriter}

  val pw = new PrintWriter(new File("/path/to/outfolder/scalemcsv_output.json"))
  pw.write(toJson(ValidationResult2Map(result)).replace("\\", "\\\\"))
  pw.close()

The resulting json file can be opened and investigated. The result for
the ``EnergySuite`` is included in the repository at ``!!!INCLUDE
HERE!!!``



Creating your own validation suite
-----------

Creating a validation suite is pretty easy. The following code
illustrates the start of a new validations suite named
YourValidationSuite::

  import scalemcsv.validator.*

  object YourValidationSuite extends scalemcsv.model.SuiteModel:
    override def suiteName: String = "YourValidationSuite"

    override def suiteSpecs: List[SuiteSpec] = List(
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
        column = "weblink",
        depends = Vector("weblink", "source"),
        rowCondition = (vals: Vector[String]) => vals(1) match {
          case "REE" => true
          case _ => false
        },
        validation = new CheckPatternMatch("link\\sunavailable".r, inverse = true)
      ),
      // More validation specifications here
    )

Each validation specification has the following parameters:

- **column**: The name of the column to perform the validation for.
- **depends**: Vector that holds the column name(s) in which reside
  the data that will be used in this validation. If some columns are
  not present in the data, the validation defined in the SuiteSpec
  will be skipped and included in the result as a
  ``CheckHeaderPresent`` validation.
- **rowCondition**: Optional. The rowCondition describes for which
  values to perform the validation. It does so by defining a lambda
  function that has a ``Vector[String]`` as input and a ``Boolean`` as
  output. How the input ``vals`` look like depends on the vector in
  the ``depends`` parameter. In the example above, the variable
  ``vals`` will be a vector like this: Vector("weblinkval",
  "sourceval"). The rowCondition will be applied to each row and only
  values for which the rowCondition returns ``true`` will be
  validated. This specific rowCondition defines that the values in the
  "weblink" column will be validated only if the value in the "source"
  column equals "REE".
- **validation**: The validation to use. See the chapter `Available
  validations`_ for the available validations and how to use them.

Use the defined validation suite on your data as follows::

  YourValidationSuite.apply(data)

  
Available validations
-----------

To make the validations in the table below work, make sure that you
have the following import at the top of the scala file where you
define your validation suite::

  import scalemcsv.validator.*

+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| validationName             | description                                                               | how to use in SuiteSpec at ``validation = ...``                                           |
+============================+===========================================================================+===========================================================================================+
| CheckAllDigits             | Checks if an incoming string consist of just numeric values               | ``validation = CheckAllDigits``                                                           |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckFloat                 | Checks if an incoming string is a float                                   | ``validation = CheckFloat``                                                               |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckPatternMatch          | Checks if the incoming string matches some regex pattern                  | ``validation = new CheckPatternMatch(pattern = "link\\sunavailable".r, inverse = false)`` |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckNotNull               | Checks if incoming string is null, na, nan or empty                       | ``validation = CheckNotNull``                                                             |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckDateNotInFuture       | Checks if incoming string is a date that is not in the future             | ``validation = new CheckDateNotInFuture(format = "yyyy-MM-dd")``                          |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckDateAGreaterThanDateB | Checks for 2 incoming strings if date A is before date B                  | ``depends = Vector(“datecolA”, “datecolB”)``                                              |
|                            |                                                                           | ``validation = new CheckDateAGreaterThanDateB(format = "yyyy-MM-dd")``                    |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckDateFormat            | Checks if incoming string is a date in the given format                   | ``validation = new CheckDateNotInFuture(format = "yyyy-MM-dd")``                          |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckNCharacters           | Checks if the incoming string is no longer than maxNChars characters (<=) | ``validation = new CheckNCharacters(50)``                                                 |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+
| CheckInRange               | Checks if the incoming string is a float within a range                   | ``validation = new CheckInRange(rangeStart = Some(0), rangeEnd = Some(10))``              |
+----------------------------+---------------------------------------------------------------------------+-------------------------------------------------------------------------------------------+


Creating your own validations
-----------

To illustrate how to create a validation, we'll be making a validation
that checks if the values in a column are exactly 10 characters long,
but also accepts value "foobar". The template for a new validation is
as follows::

  object CheckCharLengthEqualsTen extends ColumnValidation:
    override def logic(x: Vector[String]): Boolean = ???
    override def message: String = ???
    override def validationName: String = ???

We need to define the logic that computes the when the validation
gives a hit, the message to display for each hit and the name of the
validation. This can look as follows::

  class CheckCharLengthEqualsTen extends ColumnValidation:
    override def logic(x: Vector[String]): Boolean =
      // Take the first value from x (that is based on depends):
      val valToValidate = x.head

      // The logic here:
      valToValidate.length == 10
  
    override def message: String = "The value should consist of exactly 10 characters"
    
    override def validationName: String = "CheckCharLengthEqualsTen"

To then use this new validation in a validation suite, make sure to
include it as follows with the correct rowCondition::

  SuiteSpec(
    column = "myfavcolumn",
    depends = Vector("myfavcolumn"),
    rowCondition = (vals: Vector[String]) => vals.head match {
      case "foobar" => true  // skips validation
      case _ => false        // continues with CheckCharLengthEqualsTen validation
    },
    validation = new CheckCharLengthEqualsTen)

Another possibility would have been to build the "foobar" logic into
the validation itself. However, this could make the validation too
specific. The rowCondition makes it more general, because we could now
also include the rule that the value "foobaz" is also correct as follows::

  
  SuiteSpec(
    column = "myfavcolumn",
    depends = Vector("myfavcolumn"),
    rowCondition = (vals: Vector[String]) => vals.head match {
      case "foobar" => true   // skips validation
      case "foobaz" => true   // skips validation      
      case _ => false         // continues with CheckCharLengthEqualsTen validation
    },
    validation = new CheckCharLengthEqualsTen)
  

Contributing
-----------

Feel free to create a pull-request on this code-base. If you'd like,
we can connect on Discord as well. Add my via my user-name: Koen#4776

Contact: k.vandenberg@insertdata.nl
