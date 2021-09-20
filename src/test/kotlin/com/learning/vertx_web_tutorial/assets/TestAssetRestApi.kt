package com.learning.vertx_web_tutorial.assets

import com.learning.vertx_web_tutorial.AbstractRestApiTest
import com.learning.vertx_web_tutorial.broker.MainVerticle
import com.learning.vertx_web_tutorial.config.ConfigLoader
import io.netty.handler.codec.http.HttpHeaderValues
import org.junit.jupiter.api.Assertions.assertEquals
import io.vertx.core.Vertx
import io.vertx.core.http.HttpHeaders
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
class TestAssetRestApi: AbstractRestApiTest() {
  private val LOGGER = LoggerFactory.getLogger(TestAssetRestApi::class.java)

  @Test
  fun return_all_assets(vertx: Vertx, testContext: VertxTestContext) {
    val clientOptions = WebClientOptions()
    clientOptions.defaultPort = TEST_SERVER_PORT

    val client: WebClient = WebClient.create(vertx, clientOptions)
    client
      .get("/assets")
      .send()
      .onComplete(testContext.succeeding { ar ->
        val result = ar.bodyAsJsonArray()
        LOGGER.info("Test Result: $result")
        assertEquals("[{\"name\":\"AAPL\"},{\"name\":\"AMZN\"},{\"name\":\"NFLX\"},{\"name\":\"TSLA\"},{\"name\":\"MSFT\"},{\"name\":\"GOOG\"},{\"name\":\"FB\"}]", result.encode())
        assertEquals(200, ar.statusCode())
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(), ar.getHeader(HttpHeaders.CONTENT_TYPE.toString()))
        assertEquals("my-value", ar.getHeader("my-header"))
        testContext.completeNow()
      })
//      .onSuccess { resp ->
//        val result = resp.bodyAsJsonArray()
//        LOGGER.info("Success Output: ${result.encode()}")
//        try {
//          assertEquals("", result.encode())
//          testContext.completeNow()
//        }
//        catch(e: Exception) {
//          println("hmmm")
//          testContext.failNow(RuntimeException("Failed! ${e.message}"))
//        }
//      }
//      .onFailure(testContext::failNow)
  }
}
