package com.learning.vertx_web_tutorial.assets

import com.learning.vertx_web_tutorial.db.DBResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import org.slf4j.LoggerFactory

class GetAssetFromDatabaseHandler(private val db: Pool) : Handler<RoutingContext> {
  private val LOGGER = LoggerFactory.getLogger(GetAssetFromDatabaseHandler::class.java)

  override fun handle(ctx: RoutingContext) {
    db
      .query("SELECT value FROM broker.assets")
      .execute()
      .onFailure(DBResponse.errorHandler(ctx, "Failed to get asset from DB"))
      .onSuccess { result ->
        val response = JsonArray()
        result.forEach { row ->
          response.add(row.getValue("value"))
        }
        LOGGER.debug("PATH ${ctx.normalizedPath()} responds with $${response.encode()}")
        ctx
          .response()
          .setStatusCode(HttpResponseStatus.OK.code())
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }
  }

}
