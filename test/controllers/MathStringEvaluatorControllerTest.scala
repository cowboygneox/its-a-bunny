package controllers

import java.util.concurrent.TimeUnit

import org.specs2.mutable.Specification
import play.api.test.{WithServer, WsTestClient}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
  * Created by sean on 4/19/16.
  */
class MathStringEvaluatorControllerTest extends Specification {
  implicit val duration = FiniteDuration(1, TimeUnit.SECONDS)

  val validJson = {
    s"""
       |{
       |  "expression":"1+2"
       |}
       """.stripMargin
  }

  "/evaluate" should {
    "accept a properly formatted json POST" in new WithServer() {
      Await.result(WsTestClient.wsUrl("/evaluate").withHeaders("Content-Type" -> "application/json").post(validJson), duration).status must beEqualTo(202)
    }
    "reject any POST without an explicit JSON content type" in new WithServer() {
      Await.result(WsTestClient.wsUrl("/evaluate").post(validJson), duration).status must beEqualTo(415)
    }
    "reject a non-JSON POST" in new WithServer() {
      val formPayload = "query=payload&more=stuff"
      Await.result(WsTestClient.wsUrl("/evaluate").post(formPayload), duration).status must beEqualTo(415)
      Await.result(WsTestClient.wsUrl("/evaluate").withHeaders("Content-Type" -> "application/json").post(formPayload), duration).status must beEqualTo(400)
    }
  }
}
