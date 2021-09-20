package com.learning.vertx_web_tutorial.config

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.config.spi.ConfigStore
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import lombok.ToString

@ToString
class ConfigLoader {
  companion object {
    val SERVER_PORT = "SERVER_PORT"
    val DB_HOST = "DB_HOST"
    val DB_PORT = "DB_PORT"
    val DB_DATABASE = "DB_DATABASE"
    val DB_USER = "DB_USER"
    val DB_PASSWORD = "DB_PASSWORD"
    private val EXPOSED_ENV_VARS = mutableListOf(
      SERVER_PORT,
      DB_HOST,
      DB_PORT,
      DB_DATABASE,
      DB_USER,
      DB_PASSWORD
    )
    private val CONFIG_FILE = "application.yml"

    fun load(vertx: Vertx): Future<BrokerConfig> {
      val exposedKeys = JsonArray()
      EXPOSED_ENV_VARS.forEach(exposedKeys::add)

      val envStore = ConfigStoreOptions()
      envStore.type = "env"
      envStore.config = JsonObject().put("keys", exposedKeys)

      val propertyStore = ConfigStoreOptions()
      propertyStore.type = "sys"
      propertyStore.config = JsonObject().put("CACHE", false)

      val yamlStore = ConfigStoreOptions()
      yamlStore.type = "file"
      yamlStore.format = "yaml"
      yamlStore.config = JsonObject().put("path", CONFIG_FILE)

      val retriever: ConfigRetriever = ConfigRetriever.create(vertx,
        ConfigRetrieverOptions()
          .addStore(yamlStore)
          .addStore(envStore)
          .addStore(propertyStore)
      )

      return retriever.config.map(BrokerConfig::from)
    }
  }
}
