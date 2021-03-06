package controllers

import java.util.concurrent.TimeUnit

import dao.ExpressionHistoryDAO
import org.specs2.mutable.Specification
import play.api.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.test.WsTestClient
import util.ServerTest

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

/**
  * Created by sean on 4/19/16.
  */
class MathStringEvaluatorControllerTest extends Specification {
  sequential

  implicit val duration = FiniteDuration(1, TimeUnit.SECONDS)

  val validJson = {
    s"""
       |{
       |  "expression":"1+2"
       |}
       """.stripMargin
  }

  private def verifyHistoryCount(count: Int)(implicit app: Application) = {
    Await.result(app.injector.instanceOf(classOf[ExpressionHistoryDAO]).getHistory, duration).size must beEqualTo(count)
  }

  "/evaluate" should {
    "accept a properly formatted json POST" in new ServerTest() {
      Await.result(WsTestClient.wsUrl("/evaluate").withHeaders("Content-Type" -> "application/json").post(validJson), duration).status must beEqualTo(202)
      verifyHistoryCount(1)
    }
    "reject any POST without an explicit JSON content type" in new ServerTest() {
      Await.result(WsTestClient.wsUrl("/evaluate").post(validJson), duration).status must beEqualTo(415)
      verifyHistoryCount(0)
    }
    "reject a non-JSON POST" in new ServerTest() {
      val formPayload = "query=payload&more=stuff"
      Await.result(WsTestClient.wsUrl("/evaluate").post(formPayload), duration).status must beEqualTo(415)
      verifyHistoryCount(0)

      Await.result(WsTestClient.wsUrl("/evaluate").withHeaders("Content-Type" -> "application/json").post(formPayload), duration).status must beEqualTo(400)
      verifyHistoryCount(0)
    }
    "handle stress" in new ServerTest() {
      implicit val duration = FiniteDuration(30, TimeUnit.SECONDS)

      val allFutures = Future.sequence(
        (1 to 1000).map { _ =>
          WsTestClient.wsUrl("/evaluate").withHeaders("Content-Type" -> "application/json").post(validJson)
        }
      )

      Await.result(allFutures, duration)

      verifyHistoryCount(10)
    }
  }
}
