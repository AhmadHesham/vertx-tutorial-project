package com.learning.vertx_web_tutorial

import com.learning.vertx_web_tutorial.broker.MainVerticle
import com.learning.vertx_web_tutorial.config.ConfigLoader
import io.vertx.core.Vertx
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.slf4j.LoggerFactory

abstract class AbstractRestApiTest {
  protected val TEST_SERVER_PORT = 9000
  private val LOGGER = LoggerFactory.getLogger(AbstractRestApiTest::class.java)

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    System.setProperty(ConfigLoader.SERVER_PORT, TEST_SERVER_PORT.toString())
    System.setProperty(ConfigLoader.DB_HOST, "localhost")
    System.setProperty(ConfigLoader.DB_PORT, "5432")
    System.setProperty(ConfigLoader.DB_DATABASE, "vertx-stock-broker")
    System.setProperty(ConfigLoader.DB_USER, "postgres")
    System.setProperty(ConfigLoader.DB_PASSWORD, "secret")

    LOGGER.warn("Tests are using local database instance!!!")
    vertx.deployVerticle(MainVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }
}
