package eval

import eval.impl.EvalExMathStringEvaluatorException
import org.specs2.specification.core.Fragments

import scala.util.{Failure, Success, Try}

/**
  * Created by sean on 4/19/16.
  */
abstract class MathStringEvaluatorTest extends org.specs2.mutable.Specification {

  def evaluator: MathStringEvaluator

  val testCases = Seq[(String, Try[Double])] (
    // successes
    "1 + 1"         -> Success(2.0),
    "4 - 1"         -> Success(3.0),
    "3 * 12"        -> Success(36.0),
    "5 / 2"         -> Success(2.5),
    "500 * 1000"    -> Success(500000.0),
    "1.049 * 1.049" -> Success(1.0),
    "12345"         -> Success(12345.0),
    "1 + 2 + 3 + 4" -> Success(10.0),
    "2 * 3 + 4"     -> Success(10.0),

    // failures
    "2 -= 1"                          -> Failure(EvalExMathStringEvaluatorException("Unknown operator '-=' at position 3")),
    "String string = \"Some string\"" -> Failure(EvalExMathStringEvaluatorException("Unknown operator '\"' at position 17"))
  )

  s"A ${evaluator.getClass.getSimpleName}" should {
    Fragments.foreach(testCases) {
      case (expression, expectedResult) =>
        expectedResult match {
          case Success(value) =>
            s"Successfully evaluate $expression = $value" in {
              evaluator.evaluateExpression(expression) must beEqualTo(expectedResult)
            }
          case Failure(value) =>
            s"Fail to evaluate $expression = $value" in {
              evaluator.evaluateExpression(expression) must beEqualTo(expectedResult)
            }
        }

    }
  }
}
