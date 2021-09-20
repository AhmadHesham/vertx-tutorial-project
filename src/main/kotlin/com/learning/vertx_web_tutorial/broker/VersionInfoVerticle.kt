package com.learning.vertx_web_tutorial.broker

import com.learning.vertx_web_tutorial.config.ConfigLoader
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import org.slf4j.LoggerFactory

class VersionInfoVerticle: AbstractVerticle() {
  private val LOGGER = LoggerFactory.getLogger(VersionInfoVerticle::class.java)

  override fun start(startPromise: Promise<Void>) {
    ConfigLoader
      .load(vertx)
      .onFailure(startPromise::fail)
      .onSuccess { config ->
        LOGGER.debug("App Version: ${config.version}")
        startPromise.complete()
      }
  }
}
