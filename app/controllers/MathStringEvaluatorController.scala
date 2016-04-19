package controllers

import javax.inject.Singleton

import com.google.inject.Inject
import dao.ExpressionHistoryDAO
import eval.MathStringEvaluator
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by sean on 4/19/16.
  */
@Singleton
class MathStringEvaluatorController @Inject()(evaluator: MathStringEvaluator, historyDAO: ExpressionHistoryDAO) extends Controller {
  private case class Expression(expression: String)

  private implicit val expressionReads = Json.reads[Expression]

  def evaluateExpression = Action.async(parse.json) { jsResult =>
    jsResult.body.validate[Expression] match {
      case JsSuccess(Expression(expression), _) =>
        evaluator.evaluateExpression(expression) match {
          case Success(result) =>
            historyDAO.insertExpression(expression, result).map(_ => Accepted)
          case Failure(ex) =>
            Future.successful(BadRequest(Json.obj("error" -> ex.getLocalizedMessage)))
        }
      case JsError(errors)       =>
        Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }
}