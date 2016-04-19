package dao.impl

import java.sql.Connection

import anorm._
import com.google.inject.Inject
import dao.{ExpressionHistory, ExpressionHistoryDAO}
import play.api.db.Database
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


/**
  * Created by sean on 4/19/16.
  */
class PostresExpressionHistoryDAO @Inject() (database: Database) extends ExpressionHistoryDAO {

  private val maximumTableSize = 10

  override def insertExpression(expression: String, result: Double): Future[ExpressionHistory] = Future {
    database.withConnection { implicit connection =>
      val history = getHistoryInternal

      val overflow = history.drop(maximumTableSize - 1) // ensure there is room for the new value
      if (overflow.nonEmpty) {
        BatchSql("DELETE FROM ExpressionHistory WHERE id = {id}",
          overflow.map(h => NamedParameter("id", h.id))
        ).execute()
      }

      val time = System.currentTimeMillis()
      val newId = SQL("INSERT INTO ExpressionHistory (expression, result, ts) VALUES ({expression},{result},{ts})")
        .on('expression -> expression, 'result -> result, 'ts -> time)
        .executeInsert()

      ExpressionHistory(newId.get, expression, result, time)
    }
  }

  private val rowParser = Macro.namedParser[ExpressionHistory]

  private def getHistoryInternal(implicit connection: Connection): Seq[ExpressionHistory] = {
    SQL("SELECT id, expression, result, ts FROM ExpressionHistory ORDER BY ts DESC").as(rowParser.*)
  }

  override def getHistory: Future[Seq[ExpressionHistory]] = Future {
    database.withConnection { implicit connection =>
      getHistoryInternal
    }
  }
}
