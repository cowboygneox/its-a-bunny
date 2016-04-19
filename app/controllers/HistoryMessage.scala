package controllers

import dao.ExpressionHistory

/**
  * Created by sean on 4/19/16.
  */
sealed trait WebSocketMessages

case class NewHistory(history: ExpressionHistory) extends WebSocketMessages

case class AllHistory(allHistory: Seq[ExpressionHistory]) extends WebSocketMessages
