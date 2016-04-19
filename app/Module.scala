import com.google.inject.AbstractModule
import com.google.inject.name.Names
import dao.ExpressionHistoryDAO
import dao.impl.{ExpressionHistoryDAOActor, PostresExpressionHistoryDAO}
import eval.MathStringEvaluator
import eval.impl.EvalExMathStringEvaluator

/**
  * Created by sean on 4/19/16.
  */
class Module extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[MathStringEvaluator]).to(classOf[EvalExMathStringEvaluator])
    bind(classOf[ExpressionHistoryDAO]).annotatedWith(Names.named("impl")).to(classOf[PostresExpressionHistoryDAO])
    bind(classOf[ExpressionHistoryDAO]).to(classOf[ExpressionHistoryDAOActor])
  }
}
