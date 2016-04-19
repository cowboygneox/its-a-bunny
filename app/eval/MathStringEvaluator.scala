package eval

import scala.util.Try

/**
  * Created by sean on 4/19/16.
  */
trait MathStringEvaluator {
  def evaluateExpression(mathString: String): Try[Double]
}
