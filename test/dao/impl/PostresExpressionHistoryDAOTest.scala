package dao.impl

import java.util.concurrent.TimeUnit

import anorm._
import dao.ExpressionHistory
import org.specs2.mutable.Specification
import play.api.db.Database
import util.ServerTest

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
  * Created by sean on 4/19/16.
  */
class PostresExpressionHistoryDAOTest extends Specification {
  sequential

  implicit val timeout = FiniteDuration(1, TimeUnit.SECONDS)

  "A PostgreSQL expression history DAO" should {
    "insert a new expression" in new ServerTest() {
      val startTime = System.currentTimeMillis()

      val database: Database = app.injector.instanceOf(classOf[Database])

      database.withConnection { implicit connection =>
        SQL("DELETE FROM ExpressionHistory").execute()
      }

      val dao = new PostresExpressionHistoryDAO(database)
      val result = Await.result(dao.insertExpression("1 + 1", 2.0), timeout)
      result must beLike {
        case asdf: ExpressionHistory =>
          asdf.expression must beEqualTo("1 + 1")
          asdf.result must beEqualTo(2.0)
          asdf.id must beGreaterThan(0L)
          asdf.ts must beGreaterThanOrEqualTo(startTime)
      }
      val history: Seq[ExpressionHistory] = Await.result(dao.getHistory, timeout)

      history.size must beEqualTo(1)

      history.head must beEqualTo(result)
    }
    "return history descending by timestamp" in new ServerTest() {
      val startTime = System.currentTimeMillis()

      val database: Database = app.injector.instanceOf(classOf[Database])

      database.withConnection { implicit connection =>
        SQL("DELETE FROM ExpressionHistory").execute()
      }

      val dao = new PostresExpressionHistoryDAO(database)
      Await.result(dao.insertExpression("1 + 1", 2.0), timeout)
      Await.result(dao.insertExpression("1 + 2", 3.0), timeout)
      Await.result(dao.insertExpression("1 + 3", 4.0), timeout)
      val history: Seq[ExpressionHistory] = Await.result(dao.getHistory, timeout)

      history.size must beEqualTo(3)

      history.zip(history.tail).map {
        case (left, right) => left.ts >= right.ts
      }
    }
    "only persist the newest 10 results" in new ServerTest() {
      val startTime = System.currentTimeMillis()

      val database: Database = app.injector.instanceOf(classOf[Database])

      database.withConnection { implicit connection =>
        SQL("DELETE FROM ExpressionHistory").execute()
      }

      val dao = new PostresExpressionHistoryDAO(database)

      (1 to 15).foreach { i =>
        Await.result(dao.insertExpression(s"1 + $i", i + 1), timeout)
      }

      val history: Seq[ExpressionHistory] = Await.result(dao.getHistory, timeout)

      history.size must beEqualTo(10)

      history.head.expression must beEqualTo("1 + 15")
    }
  }
}
