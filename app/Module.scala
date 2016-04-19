import com.google.inject.AbstractModule
import dao.ExpressionHistoryDAO
import dao.impl.PostresExpressionHistoryDAO
import eval.MathStringEvaluator
import eval.impl.EvalExMathStringEvalulator

/**
  * Created by sean on 4/19/16.
  */
class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MathStringEvaluator]).to(classOf[EvalExMathStringEvalulator])
    bind(classOf[ExpressionHistoryDAO]).to(classOf[PostresExpressionHistoryDAO])
  }
}
