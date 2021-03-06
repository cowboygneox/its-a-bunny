package controllers

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef}
import dao.ExpressionHistory
import play.api.libs.json.Json

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
  * Created by sean on 4/19/16.
  */

class WebSocketActor(out: ActorRef, allHistory: Future[Seq[ExpressionHistory]]) extends Actor {

  implicit val ec = context.system.dispatcher

  override def preStart(): Unit = {
    context.system.eventStream.subscribe(self, classOf[WebSocketMessages])
    allHistory.onSuccess {
      case history =>
        self ! AllHistory(history)
    }
    val pingFrequency = FiniteDuration(15, TimeUnit.SECONDS)
    context.system.scheduler.schedule(pingFrequency, pingFrequency, out, Json.obj("msg" -> "keep-alive"))
  }

  override def postStop(): Unit = {
    context.system.eventStream.unsubscribe(self)
  }

  private implicit val expressionHistoryWrites = Json.writes[ExpressionHistory]
  private val newHistoryWrites = Json.writes[NewHistory]
  private val allHistoryWrites = Json.writes[AllHistory]

  private def sendMessage[T <: WebSocketMessages](message: T): Unit = {
    val json = message match {
      case m: NewHistory => newHistoryWrites.writes(m)
      case m: AllHistory => allHistoryWrites.writes(m)
    }
    out ! json
  }

  override def receive = {
    case message: WebSocketMessages => sendMessage(message)
  }
}
