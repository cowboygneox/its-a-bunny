package dao.impl

import anorm._
import dao.{ExpressionHistory, ExpressionHistoryDAO}
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


/**
  * Created by sean on 4/19/16.
  */
class PostresExpressionHistoryDAO(database: Database) extends ExpressionHistoryDAO {

  override def insertExpression(expression: String, result: Double): Future[Unit] = Future {
    database.withConnection { implicit connnection =>
      SQL("INSERT INTO ExpressionHistory (expression, result, ts) VALUES ({expression},{result},{ts})")
        .on('expression -> expression, 'result -> result, 'ts -> System.currentTimeMillis())
        .execute()
    }
  }

  private val rowParser = Macro.namedParser[ExpressionHistory]

  override def getHistory: Future[Seq[ExpressionHistory]] = Future {
    database.withConnection { implicit connection =>
      SQL("SELECT id, expression, result, ts FROM ExpressionHistory ORDER BY ts DESC").as(rowParser.*)
    }
  }
}
