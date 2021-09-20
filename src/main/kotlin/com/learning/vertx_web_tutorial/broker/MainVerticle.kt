package com.learning.vertx_web_tutorial.broker

import com.learning.vertx_web_tutorial.config.ConfigLoader
import com.learning.vertx_web_tutorial.db.migration.FlywayMigration
import io.vertx.core.*
import org.slf4j.LoggerFactory

private val LOG = LoggerFactory.getLogger(MainVerticle::class.java)

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx
      .deployVerticle(VersionInfoVerticle::class.java.name)
      .onFailure(startPromise::fail)
      .onSuccess { id ->
        LOG.info("Deployed Version Verticle with id: $id")
      }
      .compose { migrateDatabase() }
      .onFailure(startPromise::fail)
      .onSuccess { LOG.debug("Migrated database successfully!") }
      .compose { next ->
        val options = DeploymentOptions()
        options.instances = (Runtime.getRuntime().availableProcessors() / 4).coerceAtLeast(1)
        vertx
          .deployVerticle(RestApiVerticle::class.java.name, options)
          .onFailure(startPromise::fail)
          .onSuccess { id ->
            LOG.info("Deployed ${RestApiVerticle::class.java.simpleName} with ID $id")
            startPromise.complete()
          }
      }
  }

  private fun migrateDatabase(): Future<Void> {
    return ConfigLoader
      .load(vertx)
      .compose { config -> FlywayMigration.migrate(vertx, config.dbConfig!!) }
  }
}

fun main() {
  val vertx: Vertx = Vertx.vertx()

  vertx
    .deployVerticle(MainVerticle())
    .onFailure { err -> LOG.debug("An Error Occurred: $err") }
    .onSuccess { id -> LOG.debug("Deployed Verticle with ID: $id") }
}
