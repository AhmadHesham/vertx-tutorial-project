package com.learning.vertx_web_tutorial.watchlist

import com.learning.vertx_web_tutorial.db.DBResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory
import java.util.*

class GetWatchlistFromDatabaseHandler(val db: Pool) : Handler<RoutingContext> {
  private val LOGGER = LoggerFactory.getLogger(GetWatchlistFromDatabaseHandler::class.java)

  override fun handle(ctx: RoutingContext) {
    val accountId: String = ctx.pathParam("accountId")

    SqlTemplate
      .forQuery(db, "SELECT w.asset FROM broker.watchlist w WHERE w.account_id=#{account_id}")
      .mapTo(Row::toJson)
      .execute(Collections.singletonMap("account_id", accountId) as Map<String, Any>?)
      .onFailure(DBResponse.errorHandler(ctx, "Failed to fetch watchlist for accountId: $accountId"))
      .onSuccess { assets ->
        if(!assets.iterator().hasNext()) {
          DBResponse.notFoundResponse(ctx, "Watchlist for accountId: $accountId is not available")
          return@onSuccess
        }

        val response = JsonArray()
        assets.forEach(response::add)
        LOGGER.info("PATH ${ctx.normalizedPath()} responded with ${response.encode()}")
        ctx
          .response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())

      }
  }

}
