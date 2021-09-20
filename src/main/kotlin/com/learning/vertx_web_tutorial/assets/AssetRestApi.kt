package com.learning.vertx_web_tutorial.assets

import io.netty.handler.codec.http.HttpHeaderValues
import io.vertx.core.Handler
import io.vertx.core.http.HttpHeaders
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.SqlClient
import org.slf4j.LoggerFactory

class AssetRestApi {
  companion object {
    private val LOGGER = LoggerFactory.getLogger(AssetRestApi::class.java)
    val ASSETS = listOf("AAPL", "AMZN", "NFLX", "TSLA", "MSFT", "GOOG", "FB")

    fun attach(router: Router, db: Pool) {
      router.apply {
        get("/assets").handler(handleGetAssets)
        get("/pg/assets").handler(GetAssetFromDatabaseHandler(db))
      }
    }

    private val handleGetAssets = Handler<RoutingContext> { ctx ->
      val response = JsonArray()
      ASSETS.stream().map {str -> return@map Asset(str)}.forEach(response::add)
//      Thread.sleep(200)
      LOGGER.info("PATH ${ctx.normalizedPath()} responds with $${response.encode()}")
      ctx
        .response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .putHeader("my-header", "my-value")
        .end(response.toBuffer())
    }
  }
}
