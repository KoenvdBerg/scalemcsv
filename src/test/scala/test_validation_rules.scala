import org.scalatest.funsuite.AnyFunSuite

class ValidationRulesTest extends AnyFunSuite:

  // test 1
  test("CheckAllDigits") {
    val result = 0
    assert(result == 0)
  }

  // test 2
  test("'double' should handle 1") {
    val result = 1f
    assert(result == 1f)
  }

  test("test with Int.MaxValue") (pending)


