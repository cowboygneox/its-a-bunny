package dao

import scala.concurrent.Future

/**
  * Created by sean on 4/19/16.
  */
case class ExpressionHistory(id: Option[Long], expression: String, result: Double, ts: Long)

trait ExpressionHistoryDAO {
  def insertExpression(expression: String, result: Double): Future[Unit]
  def getHistory: Future[Seq[ExpressionHistory]]
}
