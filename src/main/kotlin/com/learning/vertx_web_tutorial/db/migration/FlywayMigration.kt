package com.learning.vertx_web_tutorial.db.migration

import com.learning.vertx_web_tutorial.config.DBConfig
import io.vertx.core.Future
import io.vertx.core.Vertx
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationInfo
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors

class FlywayMigration {

  companion object {
    val LOGGER = LoggerFactory.getLogger(FlywayMigration::class.java)

    fun migrate(vertx: Vertx, dbConfig: DBConfig): Future<Void> {
      LOGGER.debug("DB Config: $dbConfig")

      return vertx
        .executeBlocking<Void?> { promise ->
        execute(dbConfig)
        promise.complete()
      }
        .onFailure { err -> LOGGER.debug("Failed to migrate schema with error: $err") }
    }

    private fun execute(dbConfig: DBConfig) {
//      val database = "postgresql"
      val database = "mysql"
      val jdbcUrl = "jdbc:$database://${dbConfig.host}:${dbConfig.port}/${dbConfig.database}"
      LOGGER.debug("Migrating Schema using JDBC url: $jdbcUrl")

      val flyway = Flyway
        .configure()
        .dataSource(jdbcUrl, dbConfig.user, dbConfig.password)
        .schemas("broker")
        .defaultSchema("broker")
        .load()

      val current = flyway.info().current()
      LOGGER.info("DB Schema is at version: ${current?.version}")

      val pendingMigration = flyway.info().pending()
      LOGGER.info("Pending migrations: ${format(pendingMigration)}")

      flyway.clean()
      flyway.migrate()
    }

    private fun format(pending: Array<MigrationInfo>): String {
      if (pending == null)
        return "[]"

      return Arrays
        .stream(pending).map { each ->
          "${each.version} - ${each.description}"
        }
        .collect(Collectors.joining(",", "[", "]"))
    }
  }
}
