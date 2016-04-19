package controllers

import javax.inject.Singleton

import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc.{Action, Controller}

/**
  * Created by sean on 4/19/16.
  */
@Singleton
class MathStringEvaluatorController extends Controller {
  private case class Expression(expression: String)

  private implicit val expressionReads = Json.reads[Expression]

  def evaluateExpression = Action(parse.json) { jsResult =>
    jsResult.body.validate[Expression] match {
      case JsSuccess(request, _) => Accepted
      case JsError(errors)       => BadRequest(JsError.toJson(errors))
    }
  }
}