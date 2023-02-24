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

The scalemcsv library is in the process of being added to Maven. In
the meantime, to install and use scalemcsv, please do the following
steps:

1. Clone this git-repository to a local folder with::

     ~$ git clone git@github.com:KoenvdBerg/csv-validator.git

2. Publish the project to your local library folder::

     ~$ cd scalemcsv
     ~$ sbt publishLocal

3. Add the following line to your ``build.sbt`` file to use scalemcsv
   in a new project::

     libraryDependencies += "com.github.scalemcsv" % "scalemcsv" % "1.0.0-SNAPSHOT"

   Make sure to reload IntelliJ sbt so that IntelliJ can load the library.

4. Add this to your own code to work with scalemcsv::

     import scalemcsv.*


Get started
-----------

Validating a CSV file with scalemcsv requires that you first load in
the CSV data, and then perform the validations using a validation
suite. Below an example is provides using the ``energy_sample.csv``
data that is present in this code-repository.

Data validation can be either part of a normal workflow or part of a
ZIO workflow:

Normal workflow
~~~~~~~~~~~

Use the following code to run the validations defined in a validation
suite, which in this case is the ``EnergySuite`` that serves as
example::

  import com.github.tototoshi.csv.*
  import scalemcsv.*

  @main def run(): Unit =

    // Reading in the CSV file
    val infile: String = "/path/to/energy_sample.csv"
    implicit object MyFormat extends DefaultCSVFormat:
      override val delimiter = ';'
    val reader = CSVReader.open(infile)
    val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
    reader.close()
  
    val result = scalemcsv.suites.EnergySuite.apply(dat)
    scalemcsv.utils.utils.writeResult2Outfile(result, outfile = "/path/to/scalemcsv_output.json")


Run the main function and inspect the output at
``/path/to/scalemcsv_output.json``.

ZIO workflow
~~~~~~~~~~~

Use the following code to run the validations defined in a validation
suite, which in this case is the ``EnergySuite`` that serves as
example::

  import com.github.tototoshi.csv.*
  import zio.*
  import zio.Console.*
  import scalemcsv.*

  object RunApp extends ZIOAppDefault {
  
    // Reading in the CSV file
    val infile: String = "/path/to/energy_sample.csv"
    implicit object MyFormat extends DefaultCSVFormat:
      override val delimiter = ';'
    val reader = CSVReader.open(infile)
    val dat = scalemcsv.model.DataModel.dataMap(reader.allWithHeaders())
    reader.close()
  
    def run =
      for {
      result <- scalemcsv.suites.EnergySuite.applyWithZIO(dat, nFibers = 10)
          _ <- scalemcsv.utils.utils.writeResult2OutfileWithZIO(result, outfile = "/path/to/scalemcsv_output.json")
      } yield ()
  }


Run the main function and inspect the output at
``/path/to/scalemcsv_output.json``.

  
Investigating the results
~~~~~~~~~~~

The resulting json file can be opened and investigated. It includes
all the hits and the found erronuous values. For example for the
``CheckDateNotInFuture`` for the "reporting_date" column::

   {
    "total_errors": 2,
    "found_indices": [
      6,
      8
    ],
    "used_validation": "CheckDateNotInFuture",
    "found_values": [
      "error",
      "2030-07-08"
    ],
    "message": "Date value should be before today",
    "column": "reporting_date"
  }


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

  import scalemcsv.model.ColumnValidation

  object CheckCharLengthEqualsTen extends ColumnValidation:
    override def logic(x: Vector[String]): Boolean = ???
    override def message: String = ???
    override def validationName: String = ???

We need to define the logic that computes the when the validation
gives a hit, the message to display for each hit and the name of the
validation. This can look as follows::

  import scalemcsv.model.ColumnValidation

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


.. scala-csv: https://github.com/tototoshi/scala-csv.


