package com.learning.vertx_web_tutorial.watchlists

import com.learning.vertx_web_tutorial.AbstractRestApiTest
import com.learning.vertx_web_tutorial.assets.Asset
import com.learning.vertx_web_tutorial.broker.MainVerticle
import com.learning.vertx_web_tutorial.watchlist.WatchList
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import java.util.*

@ExtendWith(VertxExtension::class)
class TestWatchListRestApi: AbstractRestApiTest(){
  private val LOGGER = LoggerFactory.getLogger(TestWatchListRestApi::class.java)

  @Test
  fun adds_and_returns_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val clientOptions = WebClientOptions()
    clientOptions.defaultPort = TEST_SERVER_PORT

    val client: WebClient = WebClient.create(vertx, clientOptions)
    val id = UUID.randomUUID()
    client
      .put("/account/watchlist/$id")
      .sendJsonObject(WatchList(mutableListOf(Asset("AMZN"), Asset("TSLA"))).toJsonObject())
      .onComplete(testContext.succeeding { ar ->
        val result = ar.bodyAsJsonObject()
        LOGGER.info("Test Result: $result")
        Assertions.assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", result.encode())
        Assertions.assertEquals(200, ar.statusCode())
      })
      .compose { next ->
        client
          .get("/account/watchlist/$id")
          .send()
          .onComplete(testContext.succeeding { ar ->
            val result = ar.bodyAsJsonObject()
            Assertions.assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", result.encode())
            testContext.completeNow()
          })
      }

  }

  @Test
  fun adds_and_deletes_watchlist_for_account(vertx: Vertx, testContext: VertxTestContext) {
    val clientOptions = WebClientOptions()
    clientOptions.defaultPort = TEST_SERVER_PORT

    val client: WebClient = WebClient.create(vertx, clientOptions)
    val id = UUID.randomUUID()
    client
      .put("/account/watchlist/$id")
      .sendJsonObject(WatchList(mutableListOf(Asset("AMZN"), Asset("TSLA"))).toJsonObject())
      .onComplete(testContext.succeeding { ar ->
        val result = ar.bodyAsJsonObject()
        LOGGER.info("Test Result: $result")
        Assertions.assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", result.encode())
        Assertions.assertEquals(200, ar.statusCode())
      })
      .compose { next ->
        client
          .delete("/account/watchlist/$id")
          .send()
          .onComplete(testContext.succeeding { ar ->
            val result = ar.bodyAsJsonObject()
            Assertions.assertEquals("{\"assets\":[{\"name\":\"AMZN\"},{\"name\":\"TSLA\"}]}", result.encode())
            Assertions.assertEquals(200, ar.statusCode())
            testContext.completeNow()
          })
        return@compose Future.succeededFuture<String>()
      }

  }
}

