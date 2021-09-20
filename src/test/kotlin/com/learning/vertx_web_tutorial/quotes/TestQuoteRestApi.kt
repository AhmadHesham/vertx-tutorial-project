package com.learning.vertx_web_tutorial.quotes

import com.learning.vertx_web_tutorial.AbstractRestApiTest
import com.learning.vertx_web_tutorial.broker.MainVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory

@ExtendWith(VertxExtension::class)
class TestQuoteRestApi: AbstractRestApiTest() {
  private val LOGGER = LoggerFactory.getLogger(TestQuoteRestApi::class.java)

  @Test
  fun return_all_quotes(vertx: Vertx, testContext: VertxTestContext) {
    val clientOptions = WebClientOptions()
    clientOptions.defaultPort = TEST_SERVER_PORT

    val client: WebClient = WebClient.create(vertx, clientOptions)
    client
      .get("/quotes/AMZN")
      .send()
      .onComplete(testContext.succeeding { ar ->
        val result = ar.bodyAsJsonObject()
        LOGGER.info("Test Result: $result")
        assertEquals("{\"name\":\"AMZN\"}", result.getJsonObject("asset").toString())
        assertEquals(200, ar.statusCode())
        testContext.completeNow()
      })
//      .onSuccess { resp ->
//        val result = resp.bodyAsJsonObject()
//        LOGGER.info("Success Output: ${result.encode()}")
//        assertEquals("", result.encode())
//        testContext.completeNow()
//      }
//      .onFailure(testContext::failNow)
//      .onComplete { ar ->
//        if (ar.succeeded()) {
//          assertEquals("", ar.result().bodyAsJsonObject().encode())
//          testContext.completeNow()
//        }
//        else {
//          LOGGER.debug("Failed! ${ar.cause()}")
//          testContext.failNow("Failed!!!")
//        }
//      }
  }

  @Test
  fun returns_not_found_unknown(vertx: Vertx, testContext: VertxTestContext) {
    val clientOptions = WebClientOptions()
    clientOptions.defaultPort = TEST_SERVER_PORT

    val client: WebClient = WebClient.create(vertx, clientOptions)
    client
      .get("/quotes/unknown")
      .send()
      .onComplete(testContext.succeeding { ar ->
        val result = ar.bodyAsJsonObject()
        LOGGER.info("Test Result: $result")
        assertEquals("{\"msg\":\"quote for asset unknown not available\"}", result.encode())
        assertEquals(404, ar.statusCode())
        testContext.completeNow()
      })
  }
}
