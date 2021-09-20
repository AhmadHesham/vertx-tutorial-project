package com.learning.vertx_web_tutorial.quotes

import com.learning.vertx_web_tutorial.db.DBResponse
import io.netty.handler.codec.http.HttpHeaderValues
import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.RoutingContext
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import org.slf4j.LoggerFactory
import java.util.*

class GetQuotesFromDatabaseHandler(private val db: Pool) : Handler<RoutingContext> {
  private val LOGGER = LoggerFactory.getLogger(GetQuotesFromDatabaseHandler::class.java)

  override fun handle(ctx: RoutingContext) {
    val asset = ctx.pathParam("asset")
    LOGGER.debug("Asset Parameter: $asset")

    // forQuery is used when we are expecting a result or a return param
    SqlTemplate
      .forQuery(db, "SELECT q.asset, q.bid, q.ask, q.last_price, q.volume from broker.quotes q where asset=#{asset}")
      .mapTo(QuoteEntity::class.java)
      .execute(Collections.singletonMap("asset", asset) as Map<String, Any>?)
      .onFailure(DBResponse.errorHandler(ctx, "Failed to fetch quotes for asset $asset from DB"))
      .onSuccess { quotes ->
        if(!quotes.iterator().hasNext()) {
          // No Entry in DB
          DBResponse.notFoundResponse(ctx, "Quote for asset $asset is not available!")
          return@onSuccess
        }

        val response: JsonObject = quotes.iterator().next().toJson()
        LOGGER.info("Path: ${ctx.normalizedPath()} responded with $response")
        ctx
          .response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer())
      }

  }
}
