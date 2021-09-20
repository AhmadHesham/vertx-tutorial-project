package com.learning.vertx_web_tutorial.broker

import com.learning.vertx_web_tutorial.assets.AssetRestApi
import com.learning.vertx_web_tutorial.config.BrokerConfig
import com.learning.vertx_web_tutorial.config.ConfigLoader
import com.learning.vertx_web_tutorial.db.DBPools
import com.learning.vertx_web_tutorial.quotes.QuotesRestApi
import com.learning.vertx_web_tutorial.watchlist.WatchListRestApi
import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.PoolOptions
import org.slf4j.LoggerFactory

class RestApiVerticle: AbstractVerticle(){
  val LOGGER = LoggerFactory.getLogger(RestApiVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader
      .load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { config ->
        LOGGER.debug("Config Loaded: $config")
        startServer(startPromise, config)
        startPromise.complete()
      }
  }

  private fun startServer(startPromise: Promise<Void>, config: BrokerConfig) {
//    val db: Pool = DBPools.createPgPool(config, vertx)
    val db: Pool = DBPools.createMySQLPool(config, vertx)

    val router: Router = Router.router(vertx)
    router
      .route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure)
    AssetRestApi.attach(router, db)
    QuotesRestApi.attach(router, db)
    WatchListRestApi.attach(router, db)

    vertx
      .createHttpServer()
      .requestHandler(router)
      .exceptionHandler { err -> LOGGER.error("Server Error: $err") }
      .listen(config.serverPort!!) { http ->
        if (http.succeeded()) {
          LOGGER.info("HTTP server started on port ${config.serverPort}")
        } else {
          startPromise.fail(http.cause());
        }
      }
  }

  private val handleFailure = Handler<RoutingContext> { rc ->
    if (!rc.response().ended()) {
      LOGGER.error("Route Error: ${rc.failure()}")
      rc
        .response()
        .setStatusCode(500)
        .end(JsonObject().put("msg", "An error occured").toBuffer())
    }
  }
}
