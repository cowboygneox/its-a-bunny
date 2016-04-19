package util

import anorm._
import org.specs2.execute.{AsResult, Result}
import play.api.db.Database
import play.api.test.WithServer

/**
  * Created by sean on 4/19/16.
  */
abstract class ServerTest extends WithServer {
  override def around[T](t: => T)(implicit evidence$3: AsResult[T]): Result = {
    val database: Database = app.injector.instanceOf(classOf[Database])

    database.withConnection { implicit connection =>
      SQL("DELETE FROM ExpressionHistory").execute()
    }

    super.around(t)
  }
}
