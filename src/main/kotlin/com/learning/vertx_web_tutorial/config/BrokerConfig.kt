package com.learning.vertx_web_tutorial.config

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import lombok.ToString
import lombok.Value
import java.lang.RuntimeException

@Value
@ToString
class BrokerConfig {
  var serverPort: Int? = null
  var version: String? = null
  var dbConfig: DBConfig? = null

  companion object {
    fun from(config: JsonObject): BrokerConfig {
      val result = BrokerConfig()
      result.serverPort = config.getInteger("SERVER_PORT")
      result.version = config.getString("version")
      result.dbConfig = parseDbConfig(config)

      if(result.serverPort == null)
        throw RuntimeException("SERVER_PORT is not set!")

      if(result.version == null)
        throw RuntimeException("VERSION is not set!")

      return result
    }

    private fun parseDbConfig(config: JsonObject): DBConfig {
      val dbConfig = DBConfig()
      dbConfig.host = config.getString(ConfigLoader.DB_HOST)
      dbConfig.database = config.getString(ConfigLoader.DB_DATABASE)
      dbConfig.user = config.getString(ConfigLoader.DB_USER)
      dbConfig.port = config.getInteger(ConfigLoader.DB_PORT)
      dbConfig.password = config.getString(ConfigLoader.DB_PASSWORD)

      return dbConfig
    }
  }

}
