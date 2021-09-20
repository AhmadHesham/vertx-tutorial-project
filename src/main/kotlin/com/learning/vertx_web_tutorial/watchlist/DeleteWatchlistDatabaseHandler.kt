package com.learning.vertx_web_tutorial.watchlist

import com.learning.vertx_web_tutorial.db.DBResponse
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory
import java.util.*

class DeleteWatchlistDatabaseHandler(private val db: Pool) : Handler<RoutingContext> {
  private val LOGGER = LoggerFactory.getLogger(DeleteWatchlistDatabaseHandler::class.java)

  override fun handle(ctx: RoutingContext) {
    val accountId: String = ctx.pathParam("accountId")

    SqlTemplate
      .forUpdate(db, "DELETE FROM broker.watchlist WHERE account_id=#{account_id}")
      .execute(Collections.singletonMap("account_id", accountId) as Map<String, Any>?)
      .onFailure(DBResponse.errorHandler(ctx, "Failed to delete watchlist for accountId: $accountId"))
      .onSuccess { result ->
        LOGGER.debug("Deleted: ${result.rowCount()} for accountId: $accountId")

        ctx
          .response()
          .setStatusCode(HttpResponseStatus.NO_CONTENT.code())
          .end()
      }
  }

}
