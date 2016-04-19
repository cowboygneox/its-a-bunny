package eval.impl

import eval.{MathStringEvaluator, MathStringEvaluatorTest}

/**
  * Created by sean on 4/19/16.
  */
class EvalExMathStringEvalulatorTest extends MathStringEvaluatorTest {
  override lazy val evaluator: MathStringEvaluator = new EvalExMathStringEvaluator
}