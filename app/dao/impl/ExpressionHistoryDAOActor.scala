package dao.impl

import java.util.concurrent.TimeUnit
import javax.inject.Named

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern._
import com.google.inject.Inject
import dao.{ExpressionHistory, ExpressionHistoryDAO}

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

/**
  * Created by sean on 4/19/16.
  */
class ExpressionHistoryDAOActor @Inject() (@Named("impl") expressionHistoryDAO: ExpressionHistoryDAO, actorSystem: ActorSystem) extends ExpressionHistoryDAO {
  implicit val ec = actorSystem.dispatcher
  implicit val timeout = akka.util.Timeout(5, TimeUnit.SECONDS)

  private class DatabaseWorker extends Actor {
    private def wrapFuture[T](future: Future[T], sender: ActorRef = context.sender()): Unit = {
      Try {
        Await.result(future, timeout.duration )
      } match {
        case Success(value) => sender ! value
        case Failure(ex) => sender ! akka.actor.Status.Failure(ex)
      }
    }

    override def receive = {
      case (expression: String, result: Double) =>
        wrapFuture(expressionHistoryDAO.insertExpression(expression, result))
      case "getHistory" =>
        wrapFuture(expressionHistoryDAO.getHistory)
    }
  }

  val actor = actorSystem.actorOf(Props(new DatabaseWorker))

  override def insertExpression(expression: String, result: Double): Future[ExpressionHistory] = (actor ? (expression, result)).mapTo[ExpressionHistory]

  override def getHistory: Future[Seq[ExpressionHistory]] = (actor ? "getHistory").mapTo[Seq[ExpressionHistory]]
}
