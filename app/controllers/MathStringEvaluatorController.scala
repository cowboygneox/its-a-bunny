package controllers

import javax.inject.Singleton

import akka.actor.{ActorSystem, Props}
import akka.stream.Materializer
import com.google.inject.Inject
import dao.ExpressionHistoryDAO
import eval.MathStringEvaluator
import play.api.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{Action, Controller, WebSocket}

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by sean on 4/19/16.
  */
@Singleton
class MathStringEvaluatorController @Inject()(implicit evaluator: MathStringEvaluator, historyDAO: ExpressionHistoryDAO, actorSystem: ActorSystem, materializer: Materializer, configuration: Configuration) extends Controller {
  private case class Expression(expression: String)

  private implicit val expressionReads = Json.reads[Expression]

  def index = Action { implicit request =>
    Ok(views.html.index(configuration.getBoolean("application.sslmode").get))
  }

  def evaluateExpression = Action.async(parse.json) { jsResult =>
    jsResult.body.validate[Expression] match {
      case JsSuccess(Expression(expression), _) =>
        evaluator.evaluateExpression(expression) match {
          case Success(result) =>
            historyDAO.insertExpression(expression, result).map { newExpressionHistory =>
              actorSystem.eventStream.publish(NewHistory(newExpressionHistory))
              Accepted
            }
          case Failure(ex) =>
            Future.successful(BadRequest(Json.obj("error" -> ex.getLocalizedMessage)))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(JsError.toJson(errors)))
    }
  }

  def socket = WebSocket.accept[JsValue, JsValue] { request =>
    ActorFlow.actorRef(out => Props(new WebSocketActor(out, historyDAO.getHistory)))
  }
}