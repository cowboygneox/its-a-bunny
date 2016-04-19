package eval.impl

import com.udojava.evalex.Expression
import eval.MathStringEvaluator

import scala.util.{Failure, Try}

/**
  * Created by sean on 4/19/16.
  */
case class EvalExMathStringEvalulatorException(msg: String) extends RuntimeException

class EvalExMathStringEvalulator extends MathStringEvaluator {
  private def decimalPrecision = 2

  override def evaluateExpression(mathString: String): Try[Double] = {
    val expression = new Expression(mathString)

    Try {
      (expression.eval().doubleValue() * decimalPrecision).round / decimalPrecision.toDouble
    }.recoverWith {
      case ex if ex.getClass == classOf[expression.ExpressionException] =>
        Failure(EvalExMathStringEvalulatorException(ex.getLocalizedMessage))
    }
  }
}